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
- Padrão de nome: `@DisplayName("nomeDoMetodo")` na classe `@Nested`.
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

- Testes de **domínio** (agregados, Value Objects, Domain Services): sem dependência de Android ou
  infraestrutura. Objetos Kotlin puros.
- Testes de **UseCase** e **ViewModel**: todas as dependências (Repositories, DataSources) são
  mockadas. Nunca acesse banco de dados real.
- Testes de **infraestrutura** (Repository, DataSource): mockar dependências externas; DAOs que
  tocam SQLite e código abrangido por transação são cobertos por testes instrumentados
  ([0004-integration-tests.md](0004-integration-tests.md)).
- Testes de um contexto delimitado não importam lógica de negócio interna de outro contexto.

---

## 3. Erros como contrato

Erros **não são exceções** — são retornados como `AppResult.Error` com um `ApplicationError`,
um `DomainError` ou `ValidationErrors` (lista de `DomainError` da factory do agregado). Em caminhos de falha, verifique o tipo e o valor do
erro retornado, não apenas que houve erro:

```kotlin
val result = sut(command)
assertTrue(result is AppResult.Error)
assertEquals(DomainError.StayPeriod.Overbooking, (result as AppResult.Error).error)
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

- **JUnit 5** + **MockK** + asserções `kluent`.
- Criar mocks: `mockk(relaxed = true)` para mocks que não precisam de stubs explícitos. MockK
  mocka classe concreta — não crie interface só para testar.
- Coroutines: `runTest { }` para suspending functions; `@ExtendWith(MainDispatcherExtension::class)`
  quando o SUT usa `Dispatchers.Main`.
- ViewModel: ver [0005-presentation-tests.md](0005-presentation-tests.md).

---

## 7. Execução e Cobertura

```bash
./gradlew :app:testDevDebugUnitTest                        # todos os testes unitários
./gradlew :app:testDevDebugUnitTest --tests "*.NomeTest"   # teste específico
```

Cobertura: configurar via plugin Jacoco no `build.gradle.kts` do módulo `app`.
