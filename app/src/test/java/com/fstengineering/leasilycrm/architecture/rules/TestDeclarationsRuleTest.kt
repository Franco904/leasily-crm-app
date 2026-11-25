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

                    private fun createSut() = Foo()

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
        fun `Deve tratar como mock quando a propriedade só tem o sufixo Mock`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    private lateinit var sut: Foo
                    private lateinit var dependencyMock: Dependency

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
        fun `Deve tratar como mock quando a propriedade só é criada por mockk`(@TempDir dir: File) {
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
        fun `Deve verificar a ordem quando o desvio está dentro da classe aninhada`(@TempDir dir: File) {
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
        fun `Deve reportar o teste quando a instância do SUT não se chama sut`(@TempDir dir: File) {
            plant(
                dir,
                """
                class FooTest {
                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val subject = Foo(1)
                        val other = createSut()
                    }
                }
                """.trimIndent(),
            )

            val result = TestDeclarationsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `other` é a instância do SUT e deve se chamar `sut`",
                "FooTest.kt: `subject` é a instância do SUT e deve se chamar `sut`",
            )
        }

        @Test
        fun `Deve aceitar o teste quando o sut é criado por construtor ou pelo helper`(
            @TempDir dir: File,
        ) {
            plant(
                dir,
                """
                class FooTest {
                    private val sut = Foo(1)

                    @org.junit.jupiter.api.Test
                    fun `Deve fazer quando puder`() {
                        val sut = createSut()
                        val outra = FooBar(1)
                    }
                }
                """.trimIndent(),
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
        fun `Deve ignorar o arquivo quando ele está no pacote de testes de arquitetura`(
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

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, body: String, sourceSet: String = "test", pkg: String = "a.b") {
        File(dir, "src/$sourceSet/FooTest.kt").also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n$body\n")
    }
}
