# Padrões de Testes de Integração — Android Instrumented

## Escopo

Testes em `app/src/androidTest/` — executam em device físico ou emulador com contexto Android real.
Cobrem componentes que dependem do sistema operacional: DAOs Room, transações, DataStore, Keystore.

Padrões comuns a todos os níveis de teste (nomenclatura, idioma, organização interna, valores de
entrada, bons/maus testes, mocking): ver [0002-all-tests.md](0002-all-tests.md).

---

## 1. Quando usar testes instrumentados

Use `androidTest/` somente quando:

- O SUT usa `Context`, `ApplicationProvider` ou recursos Android nativos.
- O SUT acessa banco SQLite real (DAO) e o comportamento depende da persistência efetiva.
- O comportamento a ser verificado não pode ser reproduzido com mocks em testes unitários JVM.

Prefira sempre testes unitários (`test/`) quando possível — são mais rápidos e não exigem device.

---

## 2. Framework

- **JUnit 4** (`org.junit.Before`, `org.junit.Test`) — padrão para `androidTest/`.
- **Anotação de escopo:** `@LargeTest` para testes de integração que tocam infra real.
- **Asserções:** `kluent` (`shouldBe`, `shouldBeLessOrEqualTo`, `shouldNotBeNull`, etc.).
- **Context:** `ApplicationProvider.getApplicationContext<Context>()`.
- **Coroutines:** `runBlocking(Dispatchers.Default)` para suspending calls.
- **Faker e builders:** `testUtils/` de `app/src/androidTest/java/com/fstengineering/leasilycrm/`.

```kotlin
@LargeTest
class EstadiaDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var sut: EstadiaDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            AppDatabase::class.java,
        ).build()
        sut = db.estadiaDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deveRetornarEstadiasDoImovelEspecificado(): Unit =
        runBlocking(Dispatchers.Default) { ... }
}
```

**Banco real e isolado por caso de teste:** `Room.inMemoryDatabaseBuilder` no `@Before`, fechado no
`@After`. Nunca compartilhar estado de banco entre casos de teste.

---

## 3. Setup e validação (caixa cinza)

Use a API do próprio Room (DAO) para **configurar o cenário** (arrange). Para **validar os
efeitos** (assert):

1. **Prefira validar através do método de consulta do próprio SUT**, quando ele existir e expuser o
   estado necessário — mesmo em teste de integração, contornar a interface do SUT para verificar um
   estado que ela já expõe é um mau teste (ver [0002-all-tests.md](0002-all-tests.md) §3).
2. **Só recorra à leitura direta do banco (`db.query(...)` com SQL cru) quando o SUT não tiver um
   método de consulta equivalente** — comum em métodos de escrita que retornam contagem de linhas
   afetadas ou `Unit`, sem expor o valor persistido. Nesse caso, o acesso bruto não é atalho — é a
   única forma de observar o efeito.

```kotlin
@Test
fun deveTornarEstadiaRecuperavelAposInsercao(): Unit = runBlocking(Dispatchers.Default) {
    // Arrange
    db.imovelDao().insertOrUpdate(imovel)

    // Act
    val id = sut.insertOrUpdate(estadia)

    // Assert: validar estado resultante através do próprio SUT
    sut.getById(id).shouldNotBeNull()
}
```

### 3.1 Ordem de resultados

- Se a consulta sob teste não define `ORDER BY` explícito, a ordem de retorno não é garantida — não
  é contrato do SQLite, é comportamento incidental do engine.
- Não usar acesso posicional (`.first()`, `[1]`) para validar itens de uma lista nesse caso. Compare
  sem ordem — via `Set` ou matcher sem ordem:

```kotlin
result.map { it.imovelId to it.checkIn }.toSet() shouldBeEqualTo setOf(expected1, expected2)
```

---

## 4. Execução

```bash
./gradlew :app:connectedDevDebugAndroidTest   # requer device ou emulador conectado
```

Testes instrumentados são lentos — execute localmente apenas quando necessário e deixe o CI executar
na pipeline de integração.
