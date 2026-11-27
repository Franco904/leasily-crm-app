# Padrões de Testes Unitários — Android/Kotlin

## Escopo

Testes em `app/src/test/` — executam na JVM, sem device. Cobrem a lógica de negócio das menores
unidades: UseCases, ViewModels, utilitários, agregados e Value Objects.

Padrões comuns a todos os níveis de teste (nomenclatura, idioma, organização interna, valores de
entrada, bons/maus testes, mocking): ver [0002-all-tests.md](0002-all-tests.md).

---

## 1. Estrutura de Groups e Casos de Teste

- Cada método público do SUT deve ter uma classe `@Nested` própria.
- Não criar uma `@Nested` englobando toda a classe — apenas por método público.
- Padrão de nome: `@DisplayName("nomeDoMetodo")` na classe `@Nested` `<NomeDoMetodo>Tests`. O nome
  deve ser só o da função, sem receptor nem texto extra (`toBottomSheetData`, não
  `BaseError.toBottomSheetData`). Ele deve nomear uma função pública de qualquer ponto do arquivo do
  SUT (top-level, classe, `companion`, classe aninhada) ou, se o SUT só expõe propriedades, uma
  propriedade pública.
- Cada nome deve ter um único grupo: sobrecargas e mesmo nome em classes distintas do arquivo devem
  compartilhá-lo.
- O grupo deve ter ao menos um `@Test`.
- Override também conta, inclusive o de método `protected`. Propriedade não exige grupo.
- Todo `@Test` deve ficar num desses grupos, e um grupo não deve aninhar outro grupo de teste. Os
  testes de arquitetura (`…leasilycrm.architecture`) não têm SUT em `main` e ficam fora desta seção.
- Cada teste representa um caminho possível no fluxo do método, seguindo a estratégia **"Todos os
  caminhos"**.
- Evitar casos de borda muito improváveis ou sem valor de negócio.

```kotlin
@Nested
@DisplayName("invoke")
inner class InvokeTests {
    @Test
    fun `Deve registrar check-in quando Estadia está aguardando check-in`() = runTest { ... }
}
```

---

## 2. Fronteiras lógicas

- Testes de **domínio** (agregados, Value Objects, Domain Services) não devem importar `android`,
  `androidx`, `mockk`, `infra`, `application` nem `presentation`. Objetos Kotlin puros.
- Testes de **UseCase** e **ViewModel**: todas as dependências (Repositories, DataSources) são
  mockadas. Nunca acesse banco de dados real.
- Testes de **infraestrutura** (Repository, DataSource): mockar dependências externas; DAOs que
  tocam SQLite e código abrangido por transação são cobertos por testes instrumentados
  ([0004-integration-tests.md](0004-integration-tests.md)). Teste de unidade não deve
  importar `androidx.room` nem `AppDatabase`, nem ter uma classe `@Dao` como SUT.
- **Fronteira de contexto** (produção, `test/` e `androidTest/`): o módulo importa só o próprio
  módulo, os módulos do **mesmo contexto** (coluna "Contexto" da tabela de módulos do
  `README.md`), o `core` e, do pacote raiz, `Destinations` (e os gerados `R` e `BuildConfig`).
  Módulo sem contexto na tabela (`home`, `settings`, `root`) é contexto próprio. O pacote
  `testUtils` de `test/` e `androidTest/` é infra de teste: importa qualquer módulo e qualquer
  teste o importa.
- **Raiz de composição**, por papel, pode importar qualquer módulo: o pacote raiz
  (`DestinationHandling.kt`, `Destinations.kt`, `MainActivity.kt`, `LeasilyCrmApplication.kt`),
  `core/di/**` (só pacotes `.di`) e a classe `@Database` (só classes `@Dao`). O `core` não
  importa módulo fora disso.

---

## 3. Erros como contrato

Erros **não são exceções** — são retornados como `AppResult.Error` com um `ApplicationError`,
um `DomainError` ou `ValidationErrors` (lista de `DomainError` da factory do agregado). Em caminhos de falha, verifique o tipo e o valor do
erro retornado, não apenas que houve erro:

```kotlin
val result = sut(command)
result shouldBeEqualTo AppResult.Error(DomainError.StayPeriod.Overbooking)
```

---

## 4. Lógica de domínio × orquestração

- Lógica de domínio pura (factory do agregado, Value Objects) → testes próprios, isolados.
- UseCases / ViewModels testam a **orquestração** dessa lógica, não as regras em si.
- Não duplicar testes de regra de negócio nos testes de UseCase/ViewModel.

---

## 5. CQRS — leitura × escrita

- **Leitura:** verificar que os parâmetros corretos foram passados ao `ReadDataSource`; verificar o
  mapeamento da projeção; nunca verificar operações de escrita.
- **Escrita:** verificar que as operações de escrita corretas foram chamadas
  (`coVerify { estadiaRepositoryMock.save(...) }`); verificar argumentos passados após construção
  do agregado; verificar erros (`AppResult.Error`) em caminhos de falha.

---

## 6. Framework e Mocks

```kotlin
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.verify
import io.mockk.confirmVerified
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
```

- **JUnit 5** + **MockK** + asserções só `kluent` (sem `Assertions.*` nem `kotlin.test`).
- Criar mocks: `mockk(relaxUnitFun = true)`. Ele dispensa o stub de função que devolve `Unit`; o
  resto o teste declara. `relaxed = true` não existe aqui: devolve valores padrão e enfraquece o
  teste. Lambda `suspend () -> Unit` não devolve `Unit` (é `Any?`), então leva
  `coEvery { … } just runs`. MockK mocka classe concreta — não crie interface só para testar.
- Coroutines: `runTest { }` para suspending functions (`runBlocking` não existe em `test/`);
  `@ExtendWith(MainDispatcherExtension::class)` quando o SUT usa `Dispatchers.Main`.
- ViewModel: ver [0005-presentation-tests.md](0005-presentation-tests.md).

---

## 7. Execução e Cobertura

```bash
./gradlew :app:testDevDebugUnitTest                        # todos os testes unitários
./gradlew :app:testDevDebugUnitTest --tests "*.NomeTest"   # teste específico
```

Cobertura: configurar via plugin Jacoco no `build.gradle.kts` do módulo `app`.
