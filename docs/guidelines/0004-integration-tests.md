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

- **JUnit 4** (`org.junit.Before`, `org.junit.Test`) — padrão para `androidTest/`; JUnit 5 não
  entra aqui.
- **Asserções:** só `kluent` (`shouldBe`, `shouldBeLessOrEqualTo`, `shouldNotBeNull`, etc.).
- **Context:** só `ApplicationProvider.getApplicationContext<Context>()`.
- **Coroutines:** só `runBlocking(Dispatchers.Default)` para suspending calls (sem `runTest`).
- **Faker e builders:** `testUtils/` de `app/src/androidTest/java/com/fstengineering/leasilycrm/`;
  `Faker(` é construído só lá.

**Todo teste estende `IntegrationTest`**, que sobe o Koin com um banco Room in-memory novo e um
`AppDateTimeProvider` fake a cada caso e fecha o banco no `@After`. O banco é real e isolado por
caso de teste; nunca compartilhe estado entre casos. `inMemoryDatabaseBuilder`,
`@BeforeClass`, `@AfterClass` e `@ClassRule` ficam fora das classes de teste.

```kotlin
class EstadiaReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: EstadiaReadDataSource

    @Before
    fun setUp() {
        sut = db.estadiaReadDataSource()
    }

    @Test
    fun deveListarEstadiasDoImovelQuandoHaEstadiasDeOutroImovel(): Unit =
        runBlocking(Dispatchers.Default) { ... }
}
```

---

## 3. Setup e validação (caixa cinza)

Use a API do próprio Room (DAO) para **configurar o cenário** (arrange). Para **validar os
efeitos** (assert):

1. **Prefira validar através do método de consulta do próprio SUT**, quando ele existir e expuser o
   estado necessário — mesmo em teste de integração, contornar a interface do SUT para verificar um
   estado que ela já expõe é um mau teste (ver [0002-all-tests.md](0002-all-tests.md) §3).
2. **SQL cru é só para o arrange.** `execSQL` (DDL e DML, como `CREATE TRIGGER` para forçar uma
   falha) devolve `Unit` e não observa nada. Leitura crua (`rawQuery`, `query(` em
   `SupportSQLiteDatabase`/`openHelper`, literal `SELECT`/`PRAGMA`) não existe: o efeito de uma
   escrita se observa pelo método de consulta de um DataSource de leitura.

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
- Acesso posicional (`[n]`, `.get(n)`, `.elementAt`, `.last()`, `.first()`) para validar itens de
  uma lista não existe. A única exceção é `.first()` aplicado direto a `watch*(...)`, que lê o
  primeiro valor do Flow. Sem `ORDER BY`, compare sem ordem — via `Set` ou matcher sem ordem:

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
