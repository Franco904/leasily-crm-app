# Padrões de Testes de ViewModel — Android/Kotlin

## Escopo

Testes de ViewModel em `app/src/test/` — executam na JVM, sem device. Verificam estados emitidos por
`StateFlow`, eventos emitidos pelo `Channel` de `UiEvent`, e que os métodos corretos das
dependências são chamados após interações do usuário.

Padrões comuns a todos os níveis de teste: ver [0002-all-tests.md](0002-all-tests.md). Estrutura
`@Nested`, framework e mocks: ver [0003-unit-tests.md](0003-unit-tests.md).

---

## 1. Anotação obrigatória

Use sempre `@ViewModelCoroutineTest` de `testUtils/annotations/`:

```kotlin
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest

@ViewModelCoroutineTest
class PeriodoOcupadoRegistrationViewModelTest {
    private val registerEstadiaUseCaseMock: RegisterEstadiaUseCase = mockk(relaxed = true)
    private val sut = PeriodoOcupadoRegistrationViewModel(registerEstadiaUseCase = registerEstadiaUseCaseMock)

    @BeforeEach
    fun setUp() { clearAllMocks() }

    @Nested
    @DisplayName("registerEstadia")
    inner class RegisterEstadiaTests {
        @Test
        fun `Deve exibir sucesso quando Estadia for registrada`() = runTest {
            coEvery { registerEstadiaUseCaseMock(any()) } returns AppResult.Success(Unit)
            sut.registerEstadia(...)
            // verificar uiState / uiEvents
        }
    }
}
```

Essa anotação combina:

- `@TestInstance(Lifecycle.PER_CLASS)` — instância compartilhada por todos os testes da classe.
- `@ExtendWith(MainDispatcherExtension::class)` — substitui `Dispatchers.Main` pelo
  `TestDispatcher`.

---

## 2. O que verificar

- **Estados emitidos:** `uiState.value`, ou coletar o `StateFlow`/`uiEvents` com `turbine`.
- **Chamadas às dependências:** `coVerify { useCaseMock(...) }` — confirmar que o ViewModel delegou
  corretamente.
- **Argumentos:** verificar os dados passados às dependências quando envolvem regras de negócio.
- **Caminhos de falha:** quando a dependência retorna `AppResult.Error`, verificar que o ViewModel
  emite o estado de erro correspondente no `UiState`.

## 3. O que NÃO verificar

- Não testar renderização de UI (cores, visibilidade, layout) — isso pertence a testes de UI
  instrumentados.
- Não duplicar testes de regra de negócio já cobertos nos testes de UseCase/domínio — o ViewModel
  testa orquestração, não lógica de domínio.
