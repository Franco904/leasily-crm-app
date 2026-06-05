# Padrões de Testes de Integração — Android Instrumented

## Escopo

Testes em `app/src/androidTest/` — executam em device físico ou emulador com contexto Android real. Cobrem componentes que dependem do sistema operacional: DAOs do SQLite (Room), acesso a DataStore.

Testes de DAO que requerem banco Room real também pertencem aqui.

---

## 1. Quando usar testes instrumentados

Use `androidTest/` somente quando:
- O SUT usa `Context`, `ApplicationProvider` ou recursos Android nativos.
- O SUT acessa banco SQLite real (DAO impl) e o comportamento depende da persistência efetiva.
- O comportamento a ser verificado não pode ser reproduzido com mocks em testes unitários JVM.

Prefira sempre testes unitários (`test/`) quando possível — são mais rápidos e não exigem device.

---

## 2. Framework

- **JUnit 4** (`org.junit.Before`, `org.junit.Test`) — padrão para `androidTest/`.
- **Anotação de escopo:** `@LargeTest` para testes de integração que tocam infra real.
- **Asserções:** `kluent` (`shouldBe`, `shouldBeLessOrEqualTo`, `shouldNotBeNull`, etc.).
- **Context:** `ApplicationProvider.getApplicationContext<Context>()`.
- **Coroutines:** `runBlocking(Dispatchers.Default)` para suspending calls.
- **Faker:** instância global `faker` em `testUtils/fakes/Faker.kt` (quando criado: `app/src/androidTest/java/com/fstengineering/leasilycrm/testUtils/fakes/`).

---

## 3. Organização

```kotlin
@LargeTest
class EstadiasDaoImplTest {
    private lateinit var sut: EstadiasDao

    @Before
    fun setUp() {
        // montar SUT com dependências reais ou mínimas
    }

    @Test
    fun deveRetornarEstadiasDoImovelEspecificado(): Unit =
        runBlocking(Dispatchers.Default) { ... }
}
```

- Uma classe de teste por arquivo, correspondendo ao arquivo de produção.
- Métodos de teste em português, no formato camelCase descritivo (ou backtick string conforme conveniência).

---

## 4. Fakes e Builders

Usar builders em `testUtils/fakes/models/` (quando criado: `app/src/androidTest/java/com/fstengineering/leasilycrm/testUtils/fakes/models/`) para construir entidades de teste:

```kotlin
val imovel         = createFakeImovel()
val periodoOcupado = createFakePeriodoOcupado()
val estadia        = createFakeEstadia()
```

Criar novos builders neste pacote quando a entidade precisar ser reutilizada em mais de um teste.

---

## 5. Execução

```bash
./gradlew connectedAndroidTest   # requer device ou emulador conectado
```

Testes instrumentados são lentos — execute localmente apenas quando necessário e deixe o CI executar na pipeline de integração.
