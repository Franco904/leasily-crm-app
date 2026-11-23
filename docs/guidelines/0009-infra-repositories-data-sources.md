# Camada `infra/` — repositórios e fontes de dados

Convenções para a camada `infra/`: acesso a dados e serviços externos.

## Posição da camada

- `infra/` é consumida por `application/` e `presentation/`, e geralmente depende dos agregados
  em `domain/` para assegurar invariantes de negócio antes de executar operações de escrita.
- **Classes concretas, sem interface por padrão.** Interface só quando há
  polimorfismo real de runtime escolhido por DI (e.g. estratégia Online/Offline)
  — ver `0010-interface-design.md`.
- Biblioteca/toolkit externo (subdomínio Genérico) fica atrás de um Adapter fino (ver
  `0001-subdomains.md`).
- **`DataSource` é só o `@Dao` do Room.** As demais tecnologias de infra têm pacote e sufixo
  próprios em `core/infra/<tecnologia>/`: chave-valor (`localStorage/`, `*LocalStorage`),
  armazenamento compartilhado (`sharedStorage/`, `SharedStorage`), web API (`api/`, `UserApi`),
  memória (`inMemory/`, `InMemoryStorage`), além de serializers, criptografia e adapters. São
  classes concretas, sem `Impl`. Quando um `ReadModel`/write precisa de dado de 2 tecnologias, a
  composição mora um nível acima (ver "Write-side" e "Read-side"): cada tecnologia tem schema,
  modo de falha e ciclo de teste próprios.
- **Bibliotecas externas só em `infra/` e `di/`** (`android.security`, `javax.crypto`,
  `java.security`, `io.ktor`, `androidx.datastore`, `androidx.credentials`, `androidx.room`;
  `androidx.work` também na raiz). `com.google.firebase` só em `core/infra/adapters`. `*Repository`
  só existe como `<Agregado>SyncRepository` em `infra/`.

## DataSource SQL é o próprio `@Dao` do Room

- DataSource de banco é uma **`@Dao abstract class`** — não existe par `Dao` + `DataSource` +
  `Impl`. Métodos **públicos** já devolvem `AppResult`/`Flow<AppResult>`, envolvidos em
  `onDatabase {}` / `asDatabaseFlowResult()`; as queries cruas (`@Query`, `@Insert`, `@Update`)
  ficam **`protected abstract`**, invisíveis para quem chama. O Room gera o `_Impl`.

  ```kotlin
  @Dao
  abstract class EstadiaReadDataSource : KoinComponent {
      private val dateTimeProvider: AppDateTimeProvider by inject()

      fun watchCheckInCheckOutPendentes(): Flow<AppResult<List<EstadiaResumoReadModel>>> =
          observeCheckInCheckOutPendentes(dateTimeProvider.now())
              .map { orms -> orms.map { it.toEstadiaResumoReadModel() } }
              .asDatabaseFlowResult()

      @Query("SELECT ... WHERE check_in <= :now ...")
      protected abstract fun observeCheckInCheckOutPendentes(now: AppDateTime): Flow<List<EstadiaResumoOrm>>
  }
  ```

- Obtido no Koin direto do banco: `single { get<AppDatabase>().estadiaReadDataSource() }`.
- **Testado só por teste de integração** (`0004-integration-tests.md`), com
  `Room.inMemoryDatabaseBuilder(...).build().estadiaReadDataSource()`. Não há teste unitário de
  DataSource.
- Em `@Dao`: método com anotação Room (`@Query`, `@Insert`, `@Update`, `@Upsert`, `@Delete`,
  `@RawQuery`, `@Transaction`) é `protected`; `@Transaction` é `protected open suspend`; método
  público não leva anotação Room, não cita `*Orm`/`*Projection` na assinatura e é `suspend` ou
  devolve `Flow`. O pacote `infra/dataSources` não importa outra tecnologia (`datastore`, `ktor`,
  `java.io`, `android.provider`, `android.security`, `javax.crypto`).

## Injeção de dependência: só o que precisa variar sob controle

- **Construtor** só em quem é testado por **teste unitário com mock** e recebe uma dependência
  que o teste substitui: UseCase, Application Service, ViewModel e Repository recebem
  DataSources/Repositories pelo construtor.
- **`by inject()` no corpo** (`KoinComponent`, campo `private val`) para dependência que precisa
  variar sob controle em teste de integração — ex.: `AppDateTimeProvider` (instante usado num
  filtro/corte de query). Não entra na assinatura: a interface do módulo não cresce, e ninguém
  acima carrega um provider só para repassá-lo (**pass-through variable**). No `androidTest`, o
  `KoinTestRule` liga um fake. Trade-off aceito: teste de integração acoplado ao Koin.
    - `AppDateTimeProvider` **não é uma fonte de dados** — é um utilitário que embrulha uma API de
      plataforma só para permitir controle em teste. Injetá-lo num DataSource não conta como
      combinar duas tecnologias de infra.
- **Não injeta** dependência cujo comportamento é sempre o mesmo em teste e produção: utilitários
  de fronteira (`onDatabase {}`). Pagar o custo cognitivo de injeção sem ganho real de
  testabilidade é complexidade acidental.
- **Koin por papel, lista fechada**: `by inject()`, `KoinComponent`, `get()` e `GlobalContext` só
  em `di/`, `@Dao`, classes que o Android instancia (`Application`, `Activity`,
  `BroadcastReceiver`) e `object` de `core/infra/adapters` (acesso estático sem construtor; hoje o
  `ExceptionLogger`). `koinViewModel`/`koinInject` só na raiz de composição. Em classe de teste
  unitário com mock a dependência entra pelo construtor.

## Main-safety + Captura/Logging/Mapeamento de erro de biblioteca externa

- É responsabilidade desta camada garantir que chamadores de camadas acima não presenciem
  travamentos
  ou ANRs decorrentes de operações de I/O de longa duração. DAO Room com `suspend fun` ou retorno
  `Flow` já é main-safe (o Room despacha no próprio executor) — **sem `withContext`** por cima.
  `withContext(Dispatchers.IO)` só se justifica, encapsulado aqui, em API bloqueante sem suporte
  nativo a coroutine (I/O de arquivo, serialização/criptografia de DataStore).
- Todo `try`/`catch`/`runCatching`/`.catch` de exceção de biblioteca externa mora em `infra/`:
  proibido em `application/`, `domain/` e no `@Dao`; livre em `presentation/` (subtipo específico)
  e no resto da infra (adapters, serializers, `localStorage`, `api`). O `catch (e: Exception)`
  genérico existe só em `core/infra/utils/BoundaryUtils.kt`.
- Captura **só `Exception`** — `Error` da plataforma, `CancellationException` e exceções fatais
  (`SecurityException`, `GeneralSecurityException`) continuam se propagando
  (`0011-error-handling.md`, passos 3–4). Em `Flow`, use `asDatabaseFlowResult()` ou, fora
  dele, `catchException {}` (ambos em `core/infra/utils/BoundaryUtils.kt`), que já garantem isso e
  registram a falha; não use `catch {}` cru.
- O DataSource **traduz** a falha para o vocabulário da camada de cima: devolve
  vazio/`null` quando "não encontrado" é um caso normal, ou `AppResult.Error` com
  um erro do módulo da feature. Se uma camada acima está capturando `SQLiteException`,
  `IOException` ou similar, a fronteira vazou. Motivo: mantém o detalhe da biblioteca
  substituível (Adapter, `0001-subdomains.md`) e as camadas de cima testáveis sem simular
  modos de falha de infra.
- **Taxonomia de erro.** `BaseError` (`core/domain/utils/`) é uma `sealed interface`
  marcadora, com os subtipos `ApplicationError` (falha de infra), `DomainError` (regra de
  domínio) e `ValidationErrors` (lista de `DomainError` da factory).
  `BaseError` tem só esses 3 subtipos diretos. **Não carregam mensagem nem causa.** Os erros são um
  catálogo **central** em `core/domain/utils/` (kernel, ADR-0001): `ApplicationError` (enum) e
  `DomainError` (`sealed interface` com enums aninhados). Caso novo é um enum aninhado em
  `DomainError` (ou membro de `ApplicationError`) mais o mapper de UI exaustivo em
  `core/presentation`. Só `domain/` constrói `DomainError`.
- **Mensagem e causa técnica** vão para o monitoramento no `catch`, via
  `ExceptionLogger` (`core/infra/adapters/`, Adapter do subdomínio Genérico), chamado diretamente
  (`ExceptionLogger.log(e)`) só em `infra/` — nunca embutidas no erro. O `ExceptionLogger` é um
  `object` sem estado mutável que resolve um `ExceptionLoggerBackend` no Koin
  (`LogcatExceptionLoggerBackend` no app; Crashlytics entra como mais um backend) e cai em
  `NoOpExceptionLoggerBackend` quando o Koin não sobe, como nos testes JVM: o teste não verifica o
  log, porque o `BoundaryUtils` é o único `catch` genérico e sempre o aciona. Caso de falha esperada retornada como erro não precisa ser logado; falha
  mascarada precisa (`0011-error-handling.md`, passo 2).
- **Use os wrappers de `core/infra/utils/BoundaryUtils.kt`** (`onDatabase {}`,
  `asDatabaseFlowResult()`, …) em vez de reimplementar o boilerplate acima na mão. Cada um
  já encapsula tais funções. O nome do wrapper é por tecnologia ou por degrau da escada da 0011
  (`onDatabase { }` agrega em erro; `maskFailure { }` mascara com fallback;
  `onSerializationRead { }` converte em `CorruptionException` para o DataStore), mas o protocolo
  (relançar cancelamento e exceção fatal, logar) existe uma vez só — ao adicionar um wrapper novo,
  delegue a ele em vez de copiar o boilerplate.

## CQRS: dois papéis, nomes que não se misturam

| Papel   | Nome                                                                                    | Quantidade          | Entrada                 | Saída      |
|---------|-----------------------------------------------------------------------------------------|---------------------|-------------------------|------------|
| Escrita | `<Agregado>WriteDataSource` (+ `<Agregado>SyncRepository` no worker de outbox)          | 1 por agregado      | Agregado / CommandModel | signal / — |
| Leitura | `<Contexto>ReadDataSource`                                                              | por caso de leitura | QueryModel /filtro / id | ReadModel  |

### Write-side

- **Padrão: o `<Agregado>WriteDataSource` recebe o Agregado** já validado (a factory já garantiu a
  invariante — `0008-domain-aggregates-services.md`) em `save(...)`, faz o mapeamento
  Agregado → Orm (`infra/mappers/`) e persiste.
- **Transação dentro de um agregado** (ex.: Imóvel + Preços do Imóvel; excluir o pai, inserir o
  pai, obter o id gerado, inserir os filhos) mora no próprio `WriteDataSource`, num
  `@Transaction protected open suspend fun` — o Room envolve o método em transação. Um
  Repository por cima seria repasse puro (módulo raso, `0010-interface-design.md`): não crie.

  ```kotlin
  @Dao
  abstract class ImovelWriteDataSource {
      suspend fun save(imovel: Imovel): AppResult<Unit> =
          onDatabase { saveInTransaction(imovel.toImovelOrm(), imovel.precos.map { it.toImovelPrecoOrm() }) }

      @Transaction
      protected open suspend fun saveInTransaction(imovel: ImovelOrm, precos: List<ImovelPrecoOrm>) { ... }

      @Upsert
      protected abstract suspend fun upsert(imovel: ImovelOrm): Long
  }
  ```

- **Escrita que precisa chegar a outra tecnologia** (Firebase Storage, back-end Ktor): **nunca
  dual-write** no fluxo do usuário — escrever no SQL e no serviço remoto em sequência deixa os dois
  inconsistentes quando a segunda escrita falha. Use **outbox**
  ([ADR-0003](../adr/0003-outbox-para-escrita-remota.md)):
    1. O `WriteDataSource` grava o agregado **e** uma linha na tabela de outbox (status `Pending`)
       na **mesma** `@Transaction`.
    2. Um worker do WorkManager chama o `<Agregado>SyncRepository`, que consome as linhas
       `Pending`, envia ao serviço remoto e marca `Consumed` no SQL. É o único lugar em que um
       Repository coordena tecnologias distintas — e é aí que ele deixa de ser pass-through.
    3. **Idempotência**: o envio usa chave remota determinística derivada da linha de outbox (ex.:
       caminho no Storage = id da outbox). Se o processo morrer entre o envio e a marcação
       `Consumed`, o reenvio sobrescreve o mesmo objeto — o erro "enviado duas vezes" deixa de
       existir.
- Atomicidade **cross-agregado** é do **UseCase** (`0007-application-use-cases.md`), orquestrando
  `WriteDataSource`s de agregados distintos numa transação (`DatabaseAdapter.withTransaction`) —
  nunca de um DataSource ou Repository amplo cobrindo vários agregados.

### Read-side

- `<Contexto>ReadDataSource` — nome **reservado a leitura**, nunca chamado de
  "Repository"; sempre `@Dao abstract class`. Ler SQL e `DataStore` no mesmo fluxo é papel do
  ViewModel ou do UseCase (ver abaixo), não de uma classe só.
- Pode fazer JOIN cross-agregado direto no banco **quando a operação é uma
  listagem** (critério: evitar O(n) nos UseCase em cenário de múltiplas queries para construir o
  `ReadModel`).
  Fora de leitura, não cruze agregados.
- **Não valida invariante de negócio.** A leitura não passa pela factory do agregado — o dado
  persistido já foi validado na escrita. Revalidar na leitura cria um modo de falha
  (`throw`/erro) que não pode ocorrer: defina o erro fora da existência.
- Retorna o `ReadModel` (`0007-application-use-cases.md`) já montado. A projeção
  crua do banco não escapa do DataSource. Essa leveza é o CQRS a favor da leitura, não uma exceção a
  ser "corrigida".
- **SQL statement correlacionado repetido em 2+ `ReadDataSource`s**: extraia para
  `object <Contexto>CommonSqls` em `core/infra/utils/sqls/`, um `val` por statement —
  não é DataSource nem Repository, é só texto de SQL compartilhado. Documente no KDoc de
  cada `val` o alias que o statement espera de fora (ex.: `e` para a tabela de Estadia) e
  qualquer placeholder `?` posicional (ex.: `PeriodoOcupadoCommonSqls`).
- **Quando um `ReadModel` precisa de dado de 2+ `ReadDataSource`s de tecnologias
  distintas**: se é só "leia A, use o resultado como parâmetro de B" sem
  ramificação/retry/fallback, o próprio ViewModel chama as duas e combina — glue
  trivial, não é regra de negócio (`0006-presentation-jetpack-compose-mvvm.md`).
  Se há complexidade real de coordenação com regra de negócio pra esconder (mesmo teste da
  exclusão do write-side, acima), delega pra um **Use Case** ou **Application Service**
  (`0007-application-use-cases.md`) — nunca um "Repository" de leitura, nome reservado ao
  write-side.

### Verbos de leitura

Um verbo por papel. O método público e a query protegida têm verbos diferentes de propósito: o
chamador lê `get`/`watch`; quem lê o DataSource vê `select`/`observe` e sabe que ali mora o SQL.

| Papel                                 | Público (devolve `AppResult`) | `@Query` protegida |
|---------------------------------------|-------------------------------|--------------------|
| Consulta única (`suspend`)            | `get<X>`                      | `select<X>`        |
| Fluxo que re-emite quando o dado muda | `watch<X>`                    | `observe<X>`       |

- `select<X>` serve para uma linha, uma lista ou um escalar: o verbo não muda quando a cardinalidade
  muda.
- O método público tem idealmente uma linha. Desembrulhar nulo, descartar
  excluído e validação de invariante de execução pertencem ao mapper da projeção.

## Mappers de `infra/`

`infra/mappers/`, três direções:

- Agregado → Orm (+ metadado técnico: `dirty`, `serverId`)
- CommandModel → Orm — update parcial, sem reconstituir o agregado. Mesmo
  `CommandModel` de `application/` (`0007-application-use-cases.md`); não existe
  um tipo `WriteModel` dedicado — duplicaria o `CommandModel` sem ganho.
- Orm → ReadModel — reshape de projeção

Não faz CommandModel → Agregado (é `application/`,
`0007-application-use-cases.md`) nem toca `UiState` (é `presentation/`,
`0006-presentation-jetpack-compose-mvvm.md`).

### Nomes

- Só extension function top-level em `mappers/` (nada de `class`/`object`); nome
  `to<TipoDoRetorno>`: `fun ImovelOrm.toImovelResumoReadModel()`, `fun Estadia.toEstadiaOrm()`. Regra de sufixo completa em
  `0007-application-use-cases.md` ("Sufixo de tipo no nome do model").
- **Orm** = `@Entity` (Room) que espelha a tabela. É o
  **único** model de dado bruto no código; `application/` e `presentation/` nunca
  importam Orm. Todo `@Entity` termina em `Orm` e vive em `core/infra/dataSources/database/entities/`.

## Pacotes por tecnologia

- `core/infra/localStorage/{plain,encrypted}` para chave-valor (DataStore): `PlainLocalStorage`,
  `EncryptedLocalStorage`; os serializers mantêm `*Serializer`. Do módulo:
  `<modulo>/infra/localStorage/ClientesLocalStorage`.
- `core/infra/sharedStorage/SharedStorage`, `core/infra/api/` (`UserApi`, `RemoteRequest`,
  `requestDtos/`, `responseDtos/`) e `core/infra/inMemory/InMemoryStorage`: classes concretas.
- Tipo de infra (`UseCase`, `Service`, `DataSource`, `Adapter`, `Repository`, `Provider`,
  `LocalStorage`, `SharedStorage`, `Worker`) só é instanciado em `core/di/**` e na raiz de
  composição.

## Módulos diferentes sobre o mesmo agregado

e.g. Caso `imovelOcupacao/` × `periodoOcupadoRegistration/` sobre `PeriodoOcupado`:

- Query ou regra específica de um módulo mora **dentro do módulo** — inclui
  o `ReadDataSource` dele.
- O **schema** (`@Entity` / Orm) mora em `core/infra/dataSources/database/entities/`, **sem dono**:
  qualquer módulo lê ou escreve na tabela pelo seu próprio DataSource `@Dao`. Nenhum módulo redefine
  o Orm de uma tabela.
