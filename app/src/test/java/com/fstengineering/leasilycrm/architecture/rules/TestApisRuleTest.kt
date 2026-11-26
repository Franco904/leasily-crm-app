package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestApisRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando usa só APIs permitidas`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                """
                import io.mockk.coVerify
                import io.mockk.mockk

                class Planted {
                    val dependencyMock: Any = mockk(relaxUnitFun = true)
                    fun deveFazer() {
                        coVerify(exactly = 1) { dependencyMock.hashCode() }
                        val type = Planted::class.java
                    }
                }
                """.trimIndent(),
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando instrumenta o bytecode com mockk estático`(@TempDir dir: File) {
            val apis = listOf(
                "mockkStatic", "mockkObject", "mockkConstructor",
                "unmockkStatic", "unmockkObject", "unmockkConstructor",
            )
            apis.forEach { plant(dir, "src/test/$it.kt", "import io.mockk.$it\n\nclass Planted") }

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo apis.map { "$it.kt: `$it` é proibido em testes" }.sorted()
        }

        @Test
        fun `Deve reportar o teste quando verifica a contagem com limite aberto`(@TempDir dir: File) {
            val apis = listOf("atLeast", "atMost")
            apis.forEach {
                plant(dir, "src/androidTest/$it.kt", "class Planted { fun f() = verify($it = 2) { } }")
            }

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo apis.map { "$it.kt: `$it` é proibido em testes" }.sorted()
        }

        @Test
        fun `Deve reportar o teste quando verifica a ordem das chamadas`(@TempDir dir: File) {
            val apis = listOf("verifyOrder", "verifySequence", "coVerifyOrder", "coVerifySequence")
            apis.forEach { plant(dir, "src/test/$it.kt", "class Planted { fun f() = $it { } }") }

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo apis.map { "$it.kt: `$it` é proibido em testes" }.sorted()
        }

        @Test
        fun `Deve reportar o teste quando compartilha estado entre testes pelo ciclo de classe`(
            @TempDir dir: File,
        ) {
            val annotations = listOf(
                "@BeforeAll", "@AfterAll", "@BeforeClass", "@AfterClass", "@ClassRule", "@TestInstance",
            )
            annotations.forEach {
                plant(dir, "src/test/${it.drop(1)}.kt", "class Planted { $it fun f() {} }")
            }

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo annotations.map { "${it.drop(1)}.kt: `$it` é proibido em testes" }
                .sorted()
        }

        @Test
        fun `Deve aceitar o teste quando usa a preparação por teste`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                "class Planted { @BeforeEach fun a() {} @AfterEach fun b() {} @Before fun c() {} @After fun d() {} " +
                    "@BeforeAllCustom fun e() {} @TestInstanceCustom fun f() {} }",
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando importa reflection`(@TempDir dir: File) {
            val imports = listOf(
                "import java.lang.reflect.Field",
                "import kotlin.reflect.full.memberProperties",
                "import kotlin.reflect.jvm.isAccessible",
            )
            imports.forEachIndexed { index, import ->
                plant(dir, "src/test/Planted$index.kt", "$import\n\nclass Planted")
            }

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo imports.mapIndexed { index, import ->
                "Planted$index.kt: `$import` é proibido em testes"
            }
        }

        @Test
        fun `Deve reportar o teste quando acessa membro privado por reflection`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                "class Planted { fun f() = Planted::class.java.getDeclaredField(\"x\") }",
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("Planted.kt: `getDeclaredField` é proibido em testes")
        }

        @Test
        fun `Deve reportar o teste quando torna acessível um membro privado`(@TempDir dir: File) {
            plant(dir, "src/test/Planted.kt", "class Planted { fun f(field: Any) = field.setAccessible(true) }")

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("Planted.kt: `setAccessible` é proibido em testes")
        }

        @Test
        fun `Deve aceitar o teste quando um nome só começa com o limite aberto`(@TempDir dir: File) {
            plant(dir, "src/test/Planted.kt", "class Planted { fun atLeastOne() = atMostTwo(1) }")

            val result = TestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar uma vez cada API quando ela aparece mais de uma vez no arquivo`(
            @TempDir dir: File,
        ) {
            plant(dir, "src/test/Planted.kt", "class Planted { fun f() { verifyOrder { }; verifyOrder { } } }")

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("Planted.kt: `verifyOrder` é proibido em testes")
        }

        @Test
        fun `Deve ignorar o arquivo quando ele não é de teste`(@TempDir dir: File) {
            plant(dir, "src/main/Planted.kt", "class Planted { fun f() = verifyOrder { } }")

            val result = TestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando ele está no pacote de testes de arquitetura`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                "package com.fstengineering.leasilycrm.architecture.rules\n\n" +
                    "class Planted { fun f() = verifyOrder { } }",
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("Planted.kt: `verifyOrder` é proibido em testes")
        }

        @Test
        fun `Deve ignorar a API quando ela só aparece em comentário ou texto`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                "// verifyOrder foi removido\n" +
                    "/* mockkStatic\n   verifySequence */\n" +
                    "/** atLeast = 1 */\n" +
                    "class Planted {\n" +
                    "    val a = \"mockkObject\"\n" +
                    "    val b = \"\"\"\n        import java.lang.reflect.Field\n        verifyOrder { }\n    \"\"\"\n" +
                    "    val c = \"aspas \\\" escapadas mockkStatic\"\n" +
                    "}",
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar a API quando ela aparece depois de um texto na mesma linha`(@TempDir dir: File) {
            plant(
                dir,
                "src/test/Planted.kt",
                "class Planted { val a = \"texto\"; fun f() = verifyOrder { } }",
            )

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("Planted.kt: `verifyOrder` é proibido em testes")
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "src/androidTest/Zeta.kt", "class Zeta { fun f() = verifyOrder { } }")
            plant(dir, "src/test/Alfa.kt", "class Alfa { fun f() = verifySequence { } }")

            val result = TestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Alfa.kt: `verifySequence` é proibido em testes",
                "Zeta.kt: `verifyOrder` é proibido em testes",
            )
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, path: String, content: String) {
        File(dir, path).also { it.parentFile.mkdirs() }.writeText(content + "\n")
    }
}
