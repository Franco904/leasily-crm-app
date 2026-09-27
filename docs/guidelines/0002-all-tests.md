# Padrões Comuns de Teste — Android/Kotlin/MockK

## Escopo

Aplica-se a todos os níveis de teste neste projeto: unitário (`app/src/test/`), instrumentado (`app/src/androidTest/`) e ViewModel.

---

## 1. Nomenclatura e Idioma

- Descrições de teste em **português**, respeitando a linguagem do negócio.
- Kotlin: backtick string `` `Deve salvar Estadia quando dados são válidos` ``.
- `androidTest/` (JUnit 4): backtick string ou camelCase descritivo (`deveSalvarEstadiaQuandoDadosValidos`).
- Template obrigatório: `"Deve [resultado] quando [ação], dado que [contexto opcional]"`
- Basear-se em CAs da especificação quando fornecidos.
- **Proibido mencionar nomes de tipos ou valores de implementação nas descrições** — não citar enums internos, estados internos ou nomes de classes de implementação. Use o termo de negócio.

| Errado                                           | Correto                                                                      |
|--------------------------------------------------|------------------------------------------------------------------------------|
| `` `Deve retornar status = FINALIZADO` ``        | `` `Deve registrar status como 'Finalizado' quando a Estadia é encerrada` `` |
| `` `Deve emitir Loading state` ``                | `` `Deve exibir carregamento enquanto busca o histórico de Estadias` ``      |
| `` `Deve chamar estadiaDao.upsert` ``            | `` `Deve persistir Estadia quando dados são válidos` ``                      |
| `` `Deve retornar OverbookingDetected` ``        | `` `Deve rejeitar Estadia quando o período conflita com outra ocupação` ``   |

- **Corpo do teste** (variáveis, parâmetros, helpers, nomes de `inner class`) segue a regra de
  idioma do código de produção (`.claude/CLAUDE.md`): inglês, exceto termo do `CONTEXT.md`, que fica
  em português (`existingEstadia`, `validAmount`, `result`). Nunca traduzir o que é genérico:
  `val resultado`, `val erros` são **errados**.
- Nomes de mocks, fakes e classes de teste usam os termos do domínio (`estadiaRepositoryMock`,
  `createFakeImovel()`); evite `Manager`, `Helper`, `DataService`, `Json` e nomes de libs de infra.

---

## 2. Organização Interna das Declarações

Seguir esta ordem obrigatória nas declarações:

1. Variáveis de dependências mockadas (sufixo `Mock`, ex: `writeEstadiaUseCaseMock`)
2. Variável do SUT
3. Constantes e atributos de configuração
4. `@BeforeEach setUp()` / `@Before setUp()` e equivalente teardown (se necessários)
5. Métodos de teste agrupados por método público testado
6. Métodos auxiliares privados (reuso de lógica de montagem)

O mecanismo de agrupamento varia: `@Nested` (JUnit 5 — unit/ViewModel) ou comentário de bloco (JUnit 4 — androidTest).
Helpers ficam por último: quem abre o arquivo lê primeiro o contrato testado.

- Uma classe de teste por arquivo, nomeada como o arquivo de produção sufixado com `Test`
  (ex: `PeriodoOcupadoRegistrationViewModelTest.kt`).

## 2.1 Valores de entrada

Sempre que possível, use valores aleatórios para evitar acoplamento a valores fixos: instância
global `faker` de `testUtils/Faker.kt` para valores com semântica (nomes, UUIDs, endereços) e
`faker.random.nextInt(bound = 9999)` para IDs numéricos. Consulte `testUtils/fakes/` antes de criar
builders (`createFakeEstadia()`); crie um novo lá quando a montagem for reutilizada em mais de um
teste.

---

## 3. Bons e Maus Testes

**Princípio:** testes verificam comportamento através de interfaces públicas, não detalhes de implementação. O código pode mudar completamente; os testes não deveriam.

### Bons testes

- Testam comportamento que usuários/chamadores se importam
- Usam apenas a API pública do SUT
- Sobrevivem a refatorações internas
- Descrevem *o quê* o sistema faz, não *como* faz

```kotlin
// BOM: Testa comportamento observável
@Test
fun `Deve persistir Estadia quando dados são válidos`() = runTest {
    val estadia = createFakeEstadia()
    coEvery { writeEstadiaUseCaseMock.execute(any()) } returns AppResult.Success(Unit)

    sut.saveEstadia(estadia)

    coVerify { writeEstadiaUseCaseMock.execute(estadia) }
}
```

### Maus testes

Sinais de alerta:
- Mockando colaboradores internos da mesma camada
- Testando métodos privados
- Verificando contagem/ordem de chamadas sem valor de negócio
- Teste quebra ao refatorar sem o comportamento ter mudado
- Nome do teste descreve *como* em vez de *o quê*

```kotlin
// RUIM: Testa detalhes de implementação
@Test
fun `Deve chamar estadiaDao.upsert quando saveEstadia`() = runTest {
    val mockDao = mockk<EstadiaDao>()
    sut.saveEstadia(estadia)
    coVerify { mockDao.upsert(any()) }  // acoplado à impl interna
}

// RUIM: Contorna a interface para verificar estado
@Test
fun `Deve salvar no banco de dados`() = runTest {
    sut.insert(estadia)
    val cursor = db.query("SELECT * FROM estadia", null)
    assertNotNull(cursor)
}

// BOM: Verifica através da interface do DAO
@Test
fun `Deve tornar Estadia recuperável após inserção`() = runTest {
    sut.insert(estadia)
    val result = sut.findById(estadia.id)
    assertEquals(estadia.id, result?.id)
}
```

---

## 4. Quando Usar Mock

Use mock apenas em **fronteiras do sistema**:

- Back-end Ktor (sincronização) e Firebase — comunicação com serviços externos
- Banco SQLite (DAOs Room) — quando o comportamento não depende de persistência real (testes unitários JVM)
- Android `Context`, DataStore, Keystore, sistema de arquivos

**Fronteiras de camada:** UseCases (`WriteEstadiaUseCase`) e Repositories/DataSources (`EstadiaRepository`) são fronteiras do sistema do ponto de vista de quem os consome — a camada de apresentação não controla a infraestrutura. Mockáveis em testes de ViewModel e UseCase.

Não use mock em:
- Classes e colaboradores internos da mesma camada
- Qualquer coisa que você controla integralmente dentro do mesmo módulo

---

## 5. Projetando para Mockabilidade

Nas fronteiras do sistema, projete interfaces fáceis de mockar:

**Use injeção de dependência** — passe dependências externas em vez de criá-las internamente:

```kotlin
// Testável
class PeriodoOcupadoRegistrationViewModel(
    private val writeEstadiaUseCase: WriteEstadiaUseCase
) : ViewModel()

// Difícil de mockar
class PeriodoOcupadoRegistrationViewModel : ViewModel() {
    private val writeEstadiaUseCase = WriteEstadiaUseCase(/* ... */)
}
```

**Prefira interfaces específicas por operação** em vez de uma interface genérica — cada método é mockável independentemente:

```kotlin
// BOM: Cada operação é mockável independentemente
interface EstadiaRepository {
    suspend fun findById(id: String): Estadia?
    suspend fun save(estadia: Estadia)
    suspend fun delete(id: String)
}

// RUIM: Mockar requer lógica condicional dentro do mock
interface GenericRepository {
    fun execute(operation: String, params: Map<String, Any>): Any?
}
```
