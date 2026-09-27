# Camada `application/` — casos de uso e serviços de aplicação

Convenções para a camada `application/`: orquestração da intenção de negócio.

## Posição da camada

- `application/` **depende de** `domain/` e `infra/`; **nunca é dependida por** `domain/`.
- Traduz uma intenção de negócio (um ator querendo um resultado) em passo-a-passo:
  carrega agregados via `infra/`, aciona o comportamento do agregado, persiste,
  coordena transação.
- Nem todo subdomínio precisa desta camada. Os de Suporte com leitura ou
  persistência direta dispensam UseCase — o ViewModel fala direto com `infra/`
  (ver `0001-subdomains.md` e a seção read-side abaixo).

## UseCase

- **Fatiado por intenção/ator de negócio** — nunca por agregado, nunca por tela.
  "O que o profissional está tentando fazer", não "a entidade X" nem "a tela Y".
- **Um ponto de entrada público** (`operator fun invoke` ou um único método
  nomeado). Módulo profundo: superfície mínima, orquestração escondida
  (ver `0006-interface-design.md`).
- **Sem interface, sem `Impl`.** Single-module sem polimorfismo de runtime ⇒ a
  interface teria profundidade zero (`0006-interface-design.md`, YAGNI).
- **Coroutine-native**: `suspend`, sem `withContext` próprio — main-safety é
  responsabilidade da camada infra/ na ponta (`0010-infra-repositories-data-sources.md`).
- **Nunca valida invariante de negócio.** Quem valida é a factory do agregado
  (`0009-domain-aggregates-services.md`) — a regra que define se um agregado é
  válido não se repete aqui. Pequenos passos de orquestração, não invariante de negócio, podem ficar
  inline no UseCase (e.g. derivação trivial de parâmetro antes de passar adiante — coalescing,
  default, normalização de nulo — `comissao = comissaoEstadia ?: comissaoPadraoImovel`).
- **Nomenclatura**: `<VerboInglês><TermoDeDomínio>UseCase` — verbo em inglês,
  substantivo palavra por palavra do `CONTEXT.md`. Ex.: `RegisterCheckInUseCase`,
  `WriteEstadiaUseCase`. Não `RegistrarCheckIn…`, não `…StayUseCase`.

## Application Service

- **Application Service** é a categoria geral: orquestrador sem estado em
  `application/` que coordena `domain/` + `infra/` para atender a uma operação da
  aplicação. Mesmas regras do UseCase (classe concreta sem interface,
  coroutine-native, não valida invariante de negócio).
- **UseCase é a forma especializada e preferida** — cobre exatamente uma intenção
  de um ator ("o profissional quer X"). Se a operação é uma intenção única, é UseCase.
- Use o sufixo **`Service`** (`<TermoDeDomínio>Service`) quando a orquestração
  **não** mapeia para uma intenção única de ator: coordenação reaproveitada por
  vários UseCases, política de aplicação transversal (e.g. validação de sessão, permissão
  de acesso à rede por dados móveis), fachada de vários passos sem um "o ator quer" claro.
  Também é onde mora a composição de 2+ `ReadDataSource`s de tecnologias distintas
  no read-side quando essa composição tem complexidade real pra esconder (não é
  glue trivial de ViewModel) — ver "Read-side" em `0010-infra-repositories-data-sources.md`.
- Não confundir com **Domain Service** (`0009-domain-aggregates-services.md`), que é puro,
  isolado e mora em `domain/`.

## CQRS: escrita e leitura são assimétricas de propósito

A assimetria existe para **reduzir** o custo do lado de leitura, não para enriquecê-lo.

|                       | Write-side                                                          | Read-side                             |
|-----------------------|---------------------------------------------------------------------|---------------------------------------|
| UseCase               | sempre                                                              | opcional                              |
| Entrada               | CommandModel ou args diretos                                        | QueryModel, filtro/id ou args diretos |
| Fala com              | `WriteDataSource` (+ `Repository` quando há coordenação a esconder) | `ReadDataSource`                      |
| Invariante de negócio | factory do agregado valida                                          | não há — é projeção                   |

(nomes de `infra/` em `0010-infra-repositories-data-sources.md`.)

- **Write-side**: o UseCase é o **dono da transação** quando a operação exige
  atomicidade cross-agregado (ex.: reconciliação de FK) — orquestra
  `WriteDataSource`s (ou Repositories, onde existirem) de agregados distintos
  numa única transação, via o adapter de transação do banco. Nunca um Repository
  "amplo". É o padrão Unit of Work posto em prática.
- **Read-side**: só crie UseCase se houver **regra de negócio real** sobre o
  ReadModel — autorização, transformação condicional (uma transformação complexa
  que seja lógica de domínio genuína deve ser delegada a um Domain Service,
  `0009-domain-aggregates-services.md`). Teste da exclusão (de `0006-interface-design.md`):
  *existe regra que filtra/autoriza/transforma o ReadModel antes da tela, que não seja responsabilidade
  do SQL nem do ViewModel?* Se não: o ViewModel chama o `ReadDataSource` direto
  (`0007-presentation-jetpack-compose-mvvm.md`). UseCase de leitura pass-through é proibido.

## CommandModel

- **Criar quando** a operação write-side tem 4+ parâmetros primitivos, ou grupos de
  parâmetros que variam juntos. Abaixo disso: argumentos diretos no `invoke()`.
- **Não eliminar** um CommandModel só porque hoje é idêntico ao `UiState` — a
  pergunta é se há razão de divergência futura (o `UiState` acumula estado de
  apresentação, e.g. `isSubmitting`, erro por campo).
- Imutável, sem comportamento. Convertido em agregado por um mapper de
  `application/mappers/`.
- **Nome da classe carrega o sufixo `CommandModel`.**
- **Mora em `application/models/`**, junto com `ReadModel` e `QueryModel` — um
  único pacote de models por módulo, distinguidos pelo sufixo do nome.
- **Também é o DTO de update parcial em `infra/`**. Quando a operação de escrita não precisa
  reconstituir o agregado inteiro (update pontual), o mesmo `CommandModel` é a entrada do mapper
  `infra/mappers/` direto pro `Orm`.

## QueryModel

- **Criar quando** o critério de busca/filtro do read-side tem 4+ parâmetros
  primitivos, ou grupos de parâmetros que variam juntos, e atravessa camada
  (`presentation` → `application` → `infra`). Abaixo disso: `filtro`/`id` ou
  outros argumentos diretos no método do `ReadDataSource`.
- Imutável, sem comportamento — mesmo espírito do `CommandModel`, mas para o
  read-side.
- **Nome da classe carrega o sufixo `QueryModel`**.
- **Mora em `application/models/`**, junto com `ReadModel` e `CommandModel`.

## ReadModel

- Contrato de leitura: tipo de dados sem comportamento, modelado pela
  **necessidade da tela**, não pelo schema.
- Produzido pelo `ReadDataSource` (`0010-infra-repositories-data-sources.md`); consumido
  pelo ViewModel, ou por um UseCase de leitura quando este existir.
- **Nome da classe carrega o sufixo `ReadModel`** (e.g. `ImovelResumoReadModel`, nunca
  `ImovelResumo`).
- **Mora sempre em `application/models/`** — sem exceção, **inclusive em
  subdomínio de Suporte sem UseCase**: o módulo tem `application/models/` sem
  `application/usecases/`. Nunca em `infra/` (lá só mora Orm —
  `0010-infra-repositories-data-sources.md`) nem em `presentation/`.

## Sufixo de tipo no nome do model

Todo model **exceto o Agregado de domínio** (`0009-domain-aggregates-services.md`)
carrega no nome da classe o sufixo do seu tipo: `ReadModel`, `CommandModel`,
`QueryModel`, `Orm`
(`0010-infra-repositories-data-sources.md`), `UiState`
(`0007-presentation-jetpack-compose-mvvm.md`). As funções de mapper repetem esse
sufixo (`toImovelResumoReadModel()`, `toPeriodoOcupadoRegistrationUiState()`); só
`toAgregado()` fica sem. O Agregado é a única exceção porque é o modelo do
domínio, não um DTO de fronteira — a ausência de sufixo o marca. Objetivo:
nenhuma ambiguidade sobre qual camada um model pertence ao ler o nome.

## Mappers de `application/`

- `application/mappers/`: **CommandModel → Agregado**. Só reshape; a validação é
  delegada à factory do agregado.
- Extension function top-level, nome com o sufixo da origem:
  `fun EstadiaCommandModel.toEstadia()`.
- Não faz Agregado → Orm (é `infra/`, ver `0010-infra-repositories-data-sources.md`) nem
  toca `UiState` (é `presentation/`, ver `0007-presentation-jetpack-compose-mvvm.md`).
