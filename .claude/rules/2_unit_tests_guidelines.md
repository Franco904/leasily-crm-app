# Padrões de Testes Unitários — Android/Kotlin

## Escopo

Testes em `app/src/test/` — executam na JVM, sem device. Cobrem a lógica de negócio das menores unidades: UseCase impls, ViewModels, utilitários, entidades de domínio.

---

## 1. Arquivo de teste

- Uma classe de teste por arquivo, correspondendo ao arquivo de produção testado.
- Nome: arquivo de produção sufixado com `Test` (ex: `OccupiedPeriodRegistrationViewModelTest.kt`).

---

## 2. Organização Interna da Classe de Teste

Seguir esta ordem:

1. Variáveis de dependências mockadas (sufixo `Mock`, ex: `readStayMock`, `tenantQueryRepositoryMock`)
2. Variável do SUT
3. Constantes e atributos de configuração
4. `@BeforeEach setUp()` e `@AfterEach tearDown()` (se necessários)
5. Métodos auxiliares privados (reuso de lógica de montagem)
6. Classes `@Nested` por método público testado

---

## 3. Estrutura de Groups e Casos de Teste

- Cada método público do SUT deve ter uma classe `@Nested` própria.
- Não criar uma `@Nested` englobando toda a classe — apenas por método público.
- Padrão de nome: `@DisplayName("nomeDoMetodo")` na classe `@Nested`.
- Cada teste representa um caminho possível no fluxo do método, seguindo a estratégia **"Todos os caminhos"**.
- Evitar casos de borda muito improváveis ou sem valor de negócio.

```kotlin
@Nested
@DisplayName("execute")
inner class ExecuteTests {
    @Test
    fun `Deve registrar check-in quando Estadia está aguardando`() = runTest { ... }
}
```

---

## 4. Nomenclatura e Idioma

- Descrições de teste em **português**, respeitando a linguagem do negócio.
- Template obrigatório: `` `Deve [resultado] quando [ação], dado que [contexto opcional]` ``
- **Proibido mencionar nomes de tipos ou valores de implementação nas descrições** — não citar enums internos, estados de UI ou nomes de classes. Use o termo de negócio.

  | Errado | Correto |
  |---|---|
  | `` `Deve retornar Desfecho.AUSENTE` `` | `` `Deve registrar desfecho como 'Ausente' quando ninguém atendeu` `` |
  | `` `Deve emitir Loading state` `` | `` `Deve exibir carregamento enquanto busca o histórico de visitas` `` |

---

## 5. Valores de Entrada

Sempre que possível, usar valores aleatórios para evitar acoplamento a valores fixos.

- Para valores com semântica de domínio (nomes, UUIDs, endereços): use a instância global `faker` de `testUtils/Faker.kt` (diretório a criar quando os primeiros testes forem escritos):
  ```kotlin
  import com.fstengineering.leasilycrm.testUtils.faker

  val nomeRua = faker.address.streetName()
  val uuid    = faker.random.nextUUID()
  val nome    = faker.name.name()
  ```
- Para IDs numéricos simples: `faker.random.nextInt(bound = 9999)`

---

## 6. DDD (Linguagem de negócio, Lógica de negócio e Arquitetura)

### 6.1 Linguagem ubíqua

Nomes de classes de teste, variáveis, métodos e descrições devem usar os termos do domínio (ex: `PeriodoOcupado`, `periodoOcupadoDataSourceMock`). Consulte `CONTEXT.md`.

Evitar: `Business`, `DataService`, `Manager`, `Helper`, `Json`, nomes de libs de infraestrutura.

### 6.2 Fronteiras lógicas bem definidas

- Testes de **domínio** (entidades, enums): sem dependência de Android ou infraestrutura. Use objetos Kotlin/Java puros.
- Testes de **UseCase** e **ViewModel**: todas as dependências (DAOs, repositórios) devem ser mockadas. Nunca acesse banco de dados real.
- Testes de **infraestrutura** (Repository, DataSource, DAO impls): mockar dependências externas; DAOs que tocam SQLite e código abrangido por transação devem ser cobertos por testes de integração instrumentados.
- Testes de um pacote não devem importar lógica de negócio interna de outro pacote não relacionado.

### 6.3 Erros de domínio como contrato

Erros de domínio **não são exceções** — são retornados como `AppResult.Error<T, DomainError>` ou `AppResult.Error<T, BusinessRuleError>`. Em caminhos de falha, verificar o tipo e o valor do erro retornado:

```kotlin
val result = sut.execute(params)
assertTrue(result is AppResult.Error)
assertEquals(DomainError.OverbookingDetected, (result as AppResult.Error).error)
```

### 6.4 Separação: lógica de domínio × orquestração

- Lógica de domínio pura (entidades, validadores) → testes próprios, isolados.
- UseCase impls / ViewModels testam a **orquestração** dessa lógica, não as regras em si.
- Não duplicar testes de regra de negócio nos testes de UseCase/ViewModel.

### 6.5 Separação: CQRS — consulta vs comando

- **Consulta:** verificar que os parâmetros corretos foram passados ao DAO; verificar o mapeamento da projeção; nunca verificar operações de escrita.
- **Comando:** verificar que as operações de escrita corretas foram chamadas (`coVerify { dao.save(...) }`); verificar argumentos passados após construção do objeto; verificar erros (`AppResult.Error`) em caminhos de falha.

---

## 7. Framework e Mocks

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

- Criar mocks: `mockk(relaxed = true)` para mocks que não precisam de stubs explícitos.
- Coroutines: `runTest { }` para suspending functions; usar `@ExtendWith(MainDispatcherExtension::class)` quando o SUT usa `Dispatchers.Main`.
- ViewModel: usar a anotação `@ViewModelCoroutineTest` de `testUtils/annotations/`.

---

## 8. Utilitários de teste

- Consultar `testUtils/fakes/` para fakes e builders existentes antes de criar novos. Quando criado, o caminho será `app/src/test/java/com/fstengineering/leasilycrm/testUtils/fakes/`.
- Criar novos métodos auxiliares em `testUtils/` quando a lógica de montagem for reutilizável.

---

## 9. Execução e Cobertura

```bash
./gradlew test                        # todos os testes unitários
./gradlew test --tests "*.NomeTest"   # teste específico
```

Cobertura: configurar via plugin Jacoco no `build.gradle` do módulo `app`.
