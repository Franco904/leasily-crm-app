# Padrões de Testes de ViewModel — Android/Kotlin

## Escopo

Testes de ViewModel em `app/src/test/` — executam na JVM, sem device. Verificam estados emitidos por `LiveData` ou `StateFlow`, e que os métodos corretos das dependências são chamados após interações do usuário.

---

## 1. Anotação obrigatória

Use sempre `@ViewModelCoroutineTest` de `testUtils/annotations/`:

```kotlin
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest

@ViewModelCoroutineTest
class OccupiedPeriodRegistrationViewModelTest {
    ...
}
```

Essa anotação combina:
- `@TestInstance(Lifecycle.PER_CLASS)` — instância compartilhada por todos os testes da classe.
- `@ExtendWith(InstantExecutorExtension::class)` — executa `LiveData` de forma síncrona.
- `@ExtendWith(MainDispatcherExtension::class)` — substitui `Dispatchers.Main` pelo `TestDispatcher`.

---

## 2. Organização

```kotlin
@ViewModelCoroutineTest
class OccupiedPeriodRegistrationViewModelTest {
    private val registerStayUseCaseMock: RegisterStayUseCase = mockk(relaxed = true)
    private val sut = OccupiedPeriodRegistrationViewModel(registerStayUseCase = registerStayUseCaseMock)

    @BeforeEach
    fun setUp() { clearAllMocks() }

    @Nested
    @DisplayName("registerStay")
    inner class RegisterStayTests {
        @Test
        fun `Deve emitir estado de sucesso quando Estadia for registrada`() = runTest {
            coEvery { registerStayUseCaseMock.execute(any()) } returns AppResult.Success(Unit)
            sut.registerStay(...)
            // verificar estado do LiveData/StateFlow
        }
    }
}
```

---

## 3. O que verificar

- **Estados emitidos:** `LiveData.value` ou coletar `StateFlow` via `turbine` se disponível.
- **Chamadas às dependências:** `coVerify { businessMock.metodo(...) }` — confirmar que o ViewModel delegou corretamente.
- **Argumentos:** verificar os dados passados às dependências quando envolvem regras de negócio.
- **Caminhos de falha:** verificar que o estado de erro correto é emitido quando a dependência retorna `AppResult.Error` — verificar que o ViewModel emite o estado de erro correspondente no UiState.

## 4. O que NÃO verificar

- Não testar renderização de UI (binding de views, cores, visibilidade) — isso pertence a testes de UI instrumentados.
- Não duplicar testes de regra de negócio já cobertos nos testes de UseCase — o ViewModel testa orquestração, não lógica de domínio.

---

## 5. Mocks e Faker

```kotlin
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import com.fstengineering.leasilycrm.testUtils.faker
```

Use `faker` para valores de entrada com semântica de domínio. Use `mockk(relaxed = true)` para dependências cujos retornos não são relevantes para o caso específico.

---

## 6. Execução

```bash
./gradlew test   # inclui testes de ViewModel junto com os demais testes unitários
```
