# Camada `presentation/` — Jetpack Compose + MVVM

Convenções para a camada `presentation/`: telas novas em Compose seguindo MVVM.

## Posição da camada

- `presentation/` **depende de** `application/` (UseCases / Application Services) e,
  no read-side sem regra de negócio, de `infra/` direto (`ReadDataSource` — ver
  `0008-application-use-cases.md`).
- Pode tocar em `domain/` para validação parcial de campo (perda
  de foco, edição de form field, formulário em etapas).
- ViewModel expõe estado via `StateFlow`; o Composable observa. MVP não entra em
  código novo.
- **Toolkit Compose + Material Design 3**: componentes de `androidx.compose.material3`
  (`Text`, `Icon`, `Card` etc.) ou componentes compartilhados do app construídos sobre eles.
- **Model de apresentação `Parcelable` via `@Parcelize`** (ADR-0002): quando um
  `UiState`/sub-model precisa ir pro `SavedStateHandle`, usar `@Parcelize` — nunca
  `Parcelable` manual nem `java.io.Serializable`.

## ViewModel

- **Estado exposto sempre via `MutableStateFlow` privado + `StateFlow` público** (`.asStateFlow()`).
  Nunca exponha o `MutableStateFlow` diretamente — o Composable não deve poder emitir estado.

  ```kotlin
  private val _uiState = MutableStateFlow<PeriodoOcupadoRegistrationUiState>(PeriodoOcupadoRegistrationUiState.Loading)
  val uiState: StateFlow<PeriodoOcupadoRegistrationUiState> = _uiState.asStateFlow()
  ```

- **`UiState` é uma `sealed interface`**, com `Loading` como estado padrão e inicial e `Loaded` como estado final.

  ```kotlin
  sealed interface PeriodoOcupadoRegistrationUiState {
      data object Loading : PeriodoOcupadoRegistrationUiState
      data class Loaded(val imovelId: Long?, val checkIn: LocalDate?) : PeriodoOcupadoRegistrationUiState
  }
  ```

- **Sufixo `UiState` no nome de todo model de apresentação**, não só o container
  de estado da tela: o container (`<Contexto>UiState`, `sealed interface`) e cada
  sub-model que ele expõe (`EstadiaCardUiState`, `ImovelCardUiState`). Distingue
  do `ReadModel` (`0008-application-use-cases.md`) e do `Orm`
  (`0010-infra-repositories-data-sources.md`) do mesmo conceito de domínio. Regra de sufixo
  completa em `0008-application-use-cases.md` ("Sufixo de tipo no nome do model").
- **O `UiState` nunca expõe um `ReadModel` ou `Orm` direto** — cada atributo é
  transformado para a forma que a tela renderiza (formatação, `@DrawableRes` /
  `@StringRes`, derivação de flag). `ReadModel` vazando no `UiState` ⇒ a fronteira
  `presentation/` não fez seu trabalho.

- **Coleção em model de apresentação é tipo imutável de
  `kotlinx.collections.immutable`** (`ImmutableList`, `ImmutableSet`,
  `ImmutableMap` / `PersistentList`…), **nunca `List`/`Set`/`Map` da stdlib**. Vale
  para todo model de `presentation/` — o container `UiState`, cada sub-model
  (`EstadiaCardUiState`) e o tipo de retorno dos mappers de `presentation/`. O
  Compose compiler trata `List`/`Set`/`Map` como **instáveis** (a interface não
  garante imutabilidade do conteúdo), o que torna todo `@Composable` que os recebe
  não-*skippable* — recompõe mesmo sem o valor ter mudado. Os tipos de
  `kotlinx.collections.immutable` são reconhecidos como estáveis. O ViewModel monta
  a coleção com `.toImmutableList()` / `persistentListOf(...)` ao produzir o `UiState`.

- **Model de apresentação é imutável de fato: `data class` / `data object`, todo
  campo `val`, nenhum atributo `var` nem tipo mutável** (`ArrayList`,
  `mutableStateOf` embutido no state, objeto com `var`). Tipo com `var` é instável
  para o Compose compiler pela mesma razão das coleções — quebra a *skippability* e
  o `equals` estrutural que dirige a recomposição. Exceção para campos criados visando integração
  com Views legadas – ver "Interoperabilidade com View"; ter parcimônia na adoção.

- **Atualização de estado via `.update { }`, não `.value = `** — no par `MutableStateFlow` privado.
  `update` aplica a transformação de forma atômica sobre o valor vigente; `.value =` sobrescreve sem
  olhar, arriscando race condition quando duas corrotinas escrevem no mesmo `MutableStateFlow` em
  momentos próximos (a segunda escrita apaga a primeira sem compor).

  ```kotlin
  // RUIM
  _uiState.value = PeriodoOcupadoRegistrationUiState.Loaded(imovelId, checkIn)

  // BOM
  _uiState.update { PeriodoOcupadoRegistrationUiState.Loaded(imovelId, checkIn) }
  ```

  Não se aplica ao `uiState` apoiado em `savedStateHandle.getStateFlow(...)` (ver seção
  `SavedStateHandle` abaixo) — esse não expõe `.update { }`, escreve-se com `savedStateHandle[KEY] =`.
  Nesse padrão, mantenha a escrita single-writer (só o próprio `ViewModel`) para não recair na
  race condition que o `.update { }` evita.

- **Corrotinas que tocam persistência ou serviço externo demorado sempre em `viewModelScope`** —
  nunca `lifecycleScope` (Activity/Fragment) nem `rememberCoroutineScope` (Composable) para esse
  tipo de trabalho. `lifecycleScope`/`rememberCoroutineScope` são cancelados e recriados a cada
  recriação de Activity/recomposição (rotação, por exemplo — este app não usa
  `android:configChanges`, ver nota abaixo), cancelando no meio uma chamada
  de banco/rede em andamento. `viewModelScope` sobrevive porque o `ViewModel` sobrevive. Decorrência
  direta: **o ViewModel nunca expõe `suspend fun`** — quem dispara a corrotina é sempre o próprio
  ViewModel (`fun algo() { viewModelScope.launch { ... } }`), a Activity/Composable só chama funções
  síncronas.

- **Todo método público do ViewModel retorna `Unit`.** A UI se comunica com o ViewModel só por
  chamada de método (entrada) e observa `StateFlow` (estado) ou `Channel` (evento one-shot) para
  tudo que volta — sem exceção, inclusive resultado de validação parcial de campo. Método que
  retorna valor convida o chamador a ramificar sincronamente sobre ele, quebrando o fluxo
  unidirecional. Decorrência: além de não expor `suspend fun` (acima), não use expression body.
  `fun algo() = viewModelScope.launch { ... }` — retorna `Job`, que deixa a UI cancelar ou aguardar
  a corrotina que `viewModelScope` existe justamente para manter fora do alcance dela. Use block
  body com retorno implícito `Unit`.

- **Argumentos de navegação entram pelo construtor, via `SavedStateHandle`.** O app é
  single-Activity com Navigation Compose; o `ViewModel` é criado por `koinViewModel()` no
  `NavHost`, com escopo da entrada da back stack — o Koin injeta o `SavedStateHandle`, que já
  carrega os argumentos da rota. Leia-os com `savedStateHandle.toRoute<Destinations.X>()` no
  construtor. **Nunca** repasse argumentos da rota pela `Screen` até um método do ViewModel
  (pass-through), nem crie `ViewModelProvider.Factory` própria.

- **Carga assíncrona inicial em `init()` público e idempotente.** A carga que dispara
  `viewModelScope.launch` (ler do banco, observar um `Flow`) fica num `fun init()` público —
  não no bloco `init {}` do Kotlin —, para o teste controlar quando ela começa. A `Screen` chama
  `init()` num `LaunchedEffect(Unit)`, que reexecuta a cada recriação da Activity (rotação) e a
  cada retorno da tela à composição; o `ViewModel` sobrevive e recebe `init()` de novo, então
  precisa ignorar a reentrada.

- **Guarda de idempotência:**
    - `init()` que só atualiza `uiState` **sincronamente**: a guarda é o próprio estado — retorna
      cedo quando `uiState.value` já saiu do estado inicial (`Loading`). "Saiu do estado inicial"
      *é* a definição de "já inicializou".
    - `init()` que dispara carga **assíncrona**: existe uma janela entre a chamada e a corrotina
      tirar o estado de `Loading`; uma reentrada nessa janela passa pela guarda por estado e
      duplica a carga. Guarda por uma flag `isLoaded` de instância, marcada **antes** de lançar a
      corrotina — não precisa sobreviver a *process death* (o `ViewModel` é recriado do zero nesse
      caso, junto com a flag).

  ```kotlin
  class PeriodoOcupadoRegistrationViewModel(
      savedStateHandle: SavedStateHandle,
      private val readImovelDataSource: ImovelReadDataSource,
  ) : ViewModel() {
      private val args = savedStateHandle.toRoute<Destinations.PeriodoOcupadoRegistration>()

      private val _uiState = MutableStateFlow<PeriodoOcupadoRegistrationUiState>(PeriodoOcupadoRegistrationUiState.Loading)
      val uiState: StateFlow<PeriodoOcupadoRegistrationUiState> = _uiState.asStateFlow()

      private var isLoaded = false

      fun init() {
          if (isLoaded) return
          isLoaded = true

          viewModelScope.launch { loadImovel(args.imovelId) }
      }
  }

  // Screen
  LaunchedEffect(Unit) { viewModel.init() }
  ```

- **`android:configChanges` não é usado**: a flag permite contornar o ciclo de vida normal de
  restauração de estado — anti-padrão em Android. A Activity recria na rotação; a sobrevivência do
  estado lógico (formulário de Estadia preenchido) vem do `ViewModel`, não da Activity ser
  preservada. Estado só de apresentação, sem valor de negócio (scroll, accordion aberto, mês
  visível no calendário de ocupação), cabe em `rememberSaveable` no Composable — não no `ViewModel`.

- **`SavedStateHandle` pra qualquer dado de `UiState` que não possa ser recomputado do zero após
  *process death*** (o sistema mata o processo em background e recria a Activity depois — o
  `ViewModelStore` não sobrevive a isso, diferente de recriação normal). Quando o `UiState` inteiro
  é pequeno e `Parcelable`, **usar `savedStateHandle.getStateFlow(KEY, valorInicial)` como o
  próprio `uiState` público**. Escrever com `savedStateHandle[KEY] = novoValor`.

  ```kotlin
  // UiState precisa ser Parcelable pro SavedStateHandle aceitar guardá-lo — use @Parcelize
  // (ADR-0002) — não Parcelable manual nem java.io.Serializable
  sealed interface EstadiaFormUiState : Parcelable {
      @Parcelize data object Empty : EstadiaFormUiState
      @Parcelize data class Filled(val checkIn: LocalDate?, val checkOut: LocalDate?) : EstadiaFormUiState
  }

  class PeriodoOcupadoRegistrationViewModel(
      private val savedStateHandle: SavedStateHandle,
  ) : ViewModel() {
      val formUiState: StateFlow<EstadiaFormUiState> =
          savedStateHandle.getStateFlow(KEY_FORM_UI_STATE, EstadiaFormUiState.Empty)

      fun onCheckInChange(checkIn: LocalDate) {
          savedStateHandle[KEY_FORM_UI_STATE] = EstadiaFormUiState.Filled(checkIn, checkOut = null)
      }

      private companion object {
          const val KEY_FORM_UI_STATE = "formUiState"
      }
  }
  ```

  Se o `UiState` não for serializável de forma trivial (referencia algo não-Bundle-friendly, ou é
  grande demais pro Bundle de saved state), voltar pro par `MutableStateFlow`/`.update{}` privado e
  persistir só os campos primitivos relevantes em chaves separadas do `SavedStateHandle`,
  restaurando-os manualmente no valor inicial do `MutableStateFlow`.

  Nem todo estado precisa disso — só o que foi alterado por interação do usuário dentro da tela e
  não tem outra fonte de verdade (argumentos da rota já vivem no `SavedStateHandle`). Estado só de apresentação (ex.: um scroll position, um accordion aberto) cabe melhor em
  `rememberSaveable` no Composable, não em `SavedStateHandle` do ViewModel.

- **Eventos one-shot** (exibir snackbar, navegar — qualquer efeito que não é estado persistente da
  tela) são emitidos do ViewModel via `Channel(Channel.BUFFERED)` e escutados no Composable com
  `collectWithLifecycle` (`core/presentation/utils/FlowExtensions.kt`) — coleta só com a tela ao
  menos em `STARTED`, sem entregar evento com a tela em segundo plano. Nunca `.collectLatest`, que
  pode descartar eventos emitidos em rajada durante uma recomposição. O `Channel` é fechado em `onCleared()`.

  ```kotlin
  // ViewModel
  private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
  val uiEvents: Flow<UiEvent> = _uiEvents.receiveAsFlow()

  fun onOverbookingDetected() {
      _uiEvents.trySend(UiEvent.ShowOverbookingDialog)
  }

  // onCleared() é o ÚLTIMO método do ViewModel (ver abaixo)

  // Composable
  val lifecycle = LocalLifecycleOwner.current.lifecycle

  LaunchedEffect(viewModel, lifecycle) {
      viewModel.uiEvents.collectWithLifecycle(lifecycle) { event -> /* ... */ }
  }
  ```

    - **`Channel.BUFFERED`, não o default rendezvous** (`Channel<UiEvent>()`): rendezvous tem
      capacidade 0, então `send` suspende até haver um coletor. Um evento emitido de `init()` ou de
      qualquer ponto antes de o Composable anexar o `collect` travaria a coroutine emissora e ainda
      acoplaria a ordem de emissão à existência do coletor. `BUFFERED` (64 posições) desacopla:
      o evento fica retido até ser consumido.
    - **Emita com `trySend`, não `send`.** Com buffer, `trySend` não suspende, dispensa contexto de
      coroutine (pode sair direto de um método `fun` comum) e o `ChannelResult` é descartável — o
      buffer só transbordaria com 64 eventos de UI não consumidos, cenário inexistente. Reserve `send`
      para o caso raro em que a suspensão sob backpressure é intencional.

  Não é todo callback do Composable que precisa virar evento — ação puramente de UI, sem decisão de
  negócio (ex.: botão de voltar da toolbar chamando `onBack: () -> Unit` passado pela Activity), fica
  como callback direto.

- **`onCleared()` é declarado como o último método do ViewModel** — depois de `init()`, das funções
  públicas de interação e dos auxiliares privados, imediatamente antes da `sealed interface
  UiEvent`. É o encerramento do ciclo de vida; mantê-lo no fim deixa o topo da classe focado no
  contrato (estado exposto, eventos, ações).

- **`companion object`, se existir, é sempre o último componente da classe** — depois da `sealed
  interface UiEvent`, não antes. Convenção geral de Kotlin, não específica de ViewModel: o
  `companion object` é metadado estático da classe (constantes, factory), não parte do contrato de
  instância que o resto do arquivo declara.

- **Eventos declarados como `sealed interface UiEvent`, no final do ViewModel** (dentro do escopo
  da própria classe, antes do `companion object`) — um `data class`/`data object` por evento.

    - **O nome de cada membro do `UiEvent` começa com um verbo em inglês no imperativo**
      (`Show…`, `Close…`, `Finish…`, `Navigate…`, `Request…`, `Scroll…`) — nomeia a ação one-shot
      que a camada de UI deve executar ao receber o evento, não um substantivo ou estado
      (`Snackbar`, `LocationTimeout` → `ShowSnackbar`, `ShowSnackbarError`).

  ```kotlin
  class PeriodoOcupadoRegistrationViewModel : ViewModel() {
      // ... estado, init(), funções ...

      override fun onCleared() {
          _uiEvents.close()

          super.onCleared()
      }

      sealed interface UiEvent {
          data object CloseScreen : UiEvent
          data class ShowSnackbar(val messageResourceId: Int) : UiEvent
      }

      private companion object { /* chaves de SavedStateHandle, constantes */ }
  }
  ```

## Mappers de `presentation/`

- `presentation/mappers/`: **ReadModel → UiState** (container e sub-models).
  Extension function top-level, nome com o sufixo do destino:
  `fun EstadiaReadModel.toEstadiaCardUiState()`.
- É aqui que o dado ganha forma de tela — o ViewModel chama o mapper, não monta o
  `UiState` campo a campo inline quando há transformação.
- Mapper que devolve coleção devolve o tipo imutável de
  `kotlinx.collections.immutable` (ver bullet de coleção em "ViewModel"), não `List`.
- Não faz Orm → ReadModel (é `infra/`, `0010-infra-repositories-data-sources.md`) nem
  CommandModel → Agregado (é `application/`, `0008-application-use-cases.md`).

## Composable

- **Escuta de estado sempre sensível ao ciclo de vida**: `collectAsStateWithLifecycle()`, nunca
  `collectAsState()` — evita coletar (e recompor) com a tela em background.
- **`when` exaustivo sobre o `UiState`**, sem bypass por early return.

  ```kotlin
  Column {
      Toolbar(...)
      when (val state = uiState) {
          PeriodoOcupadoRegistrationUiState.Loading -> {}
          is PeriodoOcupadoRegistrationUiState.Loaded -> Content(state.imovelId, state.checkIn)
      }
  }
  ```

- **State hoisting mínimo**: passe pra baixo só as propriedades que o Composable filho realmente
  usa, nunca o `UiState` inteiro. Mantém o filho reusável e testável sem depender do shape do estado da tela pai.
  Pela mesma razão, **nunca passe um objeto `State<T>` (o `by`/`remember`/`collectAsStateWithLifecycle()`
  em si) pra baixo — sempre o valor já lido**. Um filho que recebe `State<T>` passa a decidir sozinho
  quando ler `.value` e a se tornar statefull/observador, em vez de só recompor quando o parâmetro
  muda — o objetivo do hoisting é manter os filhos **stateless**, sem saber que existe um `State` por
  trás do valor.

  ```kotlin
  // RUIM — filho recebe o State em si, vira responsável por observá-lo
  fun Parent() {
      val checkIn = remember { mutableStateOf<LocalDate?>(null) }
      Child(checkIn = checkIn)
  }

  // BOM — filho recebe só o valor, permanece stateless
  fun Parent() {
      val checkIn = remember { mutableStateOf<LocalDate?>(null) }
      Child(checkIn = checkIn.value)
  }
  ```

- **Ordem de declaração no arquivo**: funções `@Composable` primeiro (a pública, depois as privadas
  na ordem em que aparecem na árvore), funções privadas não-`@Composable` depois.

### Parâmetros de função `@Composable`

- **Ordem**: estado primeiro, callbacks depois, `modifier` como primeiro parâmetro default.
- **`modifier: Modifier = Modifier` é obrigatório em todo `@Composable` público** que emite UI —
  mesmo quando a função não tem uso óbvio pra ele hoje. Sem isso o Composable não é reutilizável
  nem posicionável pelo chamador. **Opcional em `@Composable` privado**: call site único e
  conhecido no mesmo arquivo, ajustável sem quebrar contrato externo — acrescenta quando surge
  necessidade real (reuso em 2+ lugares, ou posicionamento que o único chamador não resolve).
- **No call site, `modifier` é sempre o último argumento.**

### Encadeamento de `Modifier`

- **Sempre quebra de linha por chamada** quando o encadeamento tem mais de uma função.
