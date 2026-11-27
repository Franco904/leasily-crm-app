package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestDeclarationsRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando as declarações seguem a ordem da diretriz`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private val dependencyMock: Dependency = mockk(relaxUnitFun = true)
                    private lateinit var sut: Foo
                    private val limit = 3

                    @org.junit.jupiter.api.BeforeEach
                    fun setUp() {}

                    @org.junit.jupiter.api.AfterEach
                    fun tearDown() {}

                    @org.junit.jupiter.api.Nested
                    inner class InvokeTests {
                        @org.junit.jupiter.api.Test
                        fun `Deve fazer quando puder`() {}

                        private fun helper() {}
                    }

                    private fun createSut(id: Int) = Foo(id)

                    private companion object {
                        const val ANY = 1
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando o mock vem depois do sut`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var sut: Foo
                    private val dependencyMock: Dependency = mockk()

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `dependencyMock` (mocks) deve vir antes de `sut` (sut)",
            )
        }

        @Test
        fun `Deve reportar o mock fora de ordem quando a propriedade só tem o sufixo Mock`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var sut: Foo
                    private val dependencyMock: Dependency = createDependency()

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `dependencyMock` (mocks) deve vir antes de `sut` (sut)",
            )
        }

        @Test
        fun `Deve reportar o mock fora de ordem e sem sufixo quando a propriedade só é criada por mockk`(
            @TempDir dir: File
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var sut: Foo
                    private val dependency: Dependency = mockk()

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `dependency` é um mock e deve terminar em `Mock`",
                "FooTest.kt: em `FooTest`, `dependency` (mocks) deve vir antes de `sut` (sut)",
            )
        }

        @Test
        fun `Deve reportar uma vez o mock quando o mesmo nome se repete em testes diferentes`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val local = mockk<Dependency>()
                    }

                    @org.junit.jupiter.api.Test
                    fun `Deve desfazer quando puder`() {
                        val local = mockk<Dependency>()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `local` é um mock e deve terminar em `Mock`")
        }

        @Test
        fun `Deve reportar o teste quando a constante vem depois da preparação`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.Before
                    fun setUp() {}

                    private val limit = 3

                    @org.junit.Test
                    fun deveFazerQuandoPuder() {}
                }
                """.trimIndent(),
                sourceSet = "androidTest",
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `limit` (constantes) deve vir antes de `setUp` (setUp/tearDown)",
            )
        }

        @Test
        fun `Deve reportar o teste quando a preparação vem depois dos testes`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}

                    @org.junit.jupiter.api.BeforeEach
                    fun setUp() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `setUp` (setUp/tearDown) deve vir antes de " +
                    "`Deve fazer quando puder` (testes)",
            )
        }

        @Test
        fun `Deve reportar o teste quando o método auxiliar vem antes dos testes`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private fun helper() {}

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `Deve fazer quando puder` (testes) deve vir antes de " +
                    "`helper` (auxiliares)",
            )
        }

        @Test
        fun `Deve reportar o teste quando a classe aninhada de testes vem depois do auxiliar`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private fun helper() {}

                    @org.junit.jupiter.api.Nested
                    inner class InvokeTests {
                        @org.junit.jupiter.api.Test
                        fun `Deve fazer quando puder`() {}
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `InvokeTests` (testes) deve vir antes de `helper` (auxiliares)",
            )
        }

        @Test
        fun `Deve reportar a ordem quando o desvio está dentro da classe aninhada`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Nested
                    inner class InvokeTests {
                        private fun helper() {}

                        @org.junit.jupiter.api.Test
                        fun `Deve fazer quando puder`() {}
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `InvokeTests`, `Deve fazer quando puder` (testes) deve vir antes de " +
                    "`helper` (auxiliares)",
            )
        }

        @Test
        fun `Deve reportar o teste quando o mock é lateinit ou var`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var firstMock: Dependency
                    private var secondMock: Dependency = mockk()

                    @org.junit.jupiter.api.BeforeEach
                    fun setUp() {
                        firstMock = mockk()
                    }

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        var thirdMock = mockk<Dependency>()
                        var fourth = mockk<Dependency>()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `firstMock` é um mock e deve ser `val` inicializado na declaração",
                "FooTest.kt: `fourth` é um mock e deve ser `val` inicializado na declaração",
                "FooTest.kt: `fourth` é um mock e deve terminar em `Mock`",
                "FooTest.kt: `secondMock` é um mock e deve ser `val` inicializado na declaração",
                "FooTest.kt: `thirdMock` é um mock e deve ser `val` inicializado na declaração",
            )
        }

        @Test
        fun `Deve aceitar o teste quando o mock é val inicializado na declaração`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private val dependencyMock: Dependency = mockk(relaxUnitFun = true)
                    private lateinit var sut: Foo

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val localMock = mockk<Dependency>()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando o mock não termina em Mock`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private val dependency: Dependency = mockk()
                    private val other: Dependency =
                        mockk(relaxUnitFun = true)

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val local = mockk<Dependency>()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `dependency` é um mock e deve terminar em `Mock`",
                "FooTest.kt: `local` é um mock e deve terminar em `Mock`",
                "FooTest.kt: `other` é um mock e deve terminar em `Mock`",
            )
        }

        @Test
        fun `Deve reportar o teste quando a instância criada pelo helper do SUT não se chama sut`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val other = createSut()
                        val sut = createSut()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `other` é a instância do SUT e deve se chamar `sut`",
            )
        }

        @Test
        fun `Deve aceitar o teste quando outras instâncias do tipo testado são montagem do cenário`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private val sut = Foo(1)

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val expected = Foo(2)
                        val outra = FooBar(1)
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o parametrizado vem junto dos testes`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.params.ParameterizedTest
                    fun a() {}

                    @org.junit.jupiter.api.RepeatedTest(2)
                    fun b() {}

                    @org.junit.jupiter.api.TestFactory
                    fun c() {}

                    @org.junit.jupiter.api.TestTemplate
                    fun d() {}

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}

                    private fun helper() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar a propriedade quando o mockk só aparece dentro de outra chamada`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var sut: Foo
                    private val items = listOf(mockk<Dependency>())

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o mock e o sut quando só aparecem em comentário ou texto`(@TempDir dir: File) {
            plant(
                dir,
                "class FooTest {\n" +
                    "    // var other = mockk<Dependency>(); val x = createSut()\n" +
                    "    private val text = \"var other = mockk<Dependency>(); val y = createSut()\"\n\n" +
                    "    @org.junit.jupiter.api.Test\n" +
                    "    fun `Deve fazer quando puder`() {}\n" +
                    "}",
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o arquivo quando ele não tem teste`(@TempDir dir: File) {
            plant(dir, "class FooTest { private fun helper() {}\n    private val limit = 3 }")

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar a ordem quando o arquivo está no pacote de testes de arquitetura`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private fun helper() {}

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}
                }
                """.trimIndent(),
                pkg = "com.fstengineering.leasilycrm.architecture.rules",
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: em `FooTest`, `Deve fazer quando puder` (testes) deve vir antes de " +
                    "`helper` (auxiliares)",
            )
        }

        @Test
        fun `Deve reportar o helper do SUT quando ele não tem parâmetro`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}

                    private fun createSut() = Foo()

                    private fun createLoadedSut(): Foo = createSut()
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                parameterless("createLoadedSut"),
                parameterless("createSut"),
            )
        }

        @Test
        fun `Deve reportar o helper do SUT quando ele está no grupo de um método`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Nested
                    inner class InvokeTests {
                        @org.junit.jupiter.api.Test
                        fun `Deve fazer quando puder`() {}

                        private fun createSut() = Foo()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(parameterless("createSut"))
        }

        @Test
        fun `Deve aceitar o helper do SUT quando a montagem varia por parâmetro`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {}

                    private fun createSut(id: Long? = null) = Foo(id)

                    private fun createLoadedSut(name: String) = Foo(name)

                    private fun createForm() = Form()
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o arquivo quando ele não é de teste`(@TempDir dir: File) {
            plant(
                dir,
                "class FooTest { private fun helper() {}\n    @org.junit.Test fun deveFazerQuandoPuder() {} }",
                sourceSet = "main",
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }
    }

    private fun parameterless(name: String) =
        "FooTest.kt: `$name` sem parâmetro não deve existir: o `sut` deve ser declarado já " +
            "construído ou montado no `@BeforeEach` (`@Before` no instrumentado)"

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, body: String, sourceSet: String = "test", pkg: String = "a.b") {
        File(dir, "src/$sourceSet/FooTest.kt").also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n$body\n")
    }
}
