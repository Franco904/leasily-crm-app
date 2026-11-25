package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestNamesRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando o nome segue o template de test`(@TempDir dir: File) {
            plantUnitTests(
                dir,
                "Deve salvar Estadia quando dados são válidos",
                "Deve rejeitar Estadia quando o período conflita, dado que há outra ocupação",
            )

            val result = TestNamesRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando o nome de test não começa com Deve`(@TempDir dir: File) {
            plantUnitTests(dir, "Salva Estadia quando dados são válidos")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `Salva Estadia quando dados são válidos` foge do template " +
                    "`Deve [resultado] quando [ação]` de test/",
            )
        }

        @Test
        fun `Deve reportar o teste quando o nome de test não tem a condição Quando`(@TempDir dir: File) {
            plantUnitTests(dir, "Deve salvar Estadia")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `Deve salvar Estadia` foge do template " +
                    "`Deve [resultado] quando [ação]` de test/",
            )
        }

        @Test
        fun `Deve reportar o teste quando o nome unitário usa a notação sem espaços`(@TempDir dir: File) {
            File(dir, "src/test/Planted.kt").also { it.parentFile.mkdirs() }.writeText(
                "package p\n\nclass Planted {\n    @org.junit.jupiter.api.Test\n" +
                    "    fun deveSalvarQuandoValido() {}\n}\n",
            )

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `deveSalvarQuandoValido` foge do template " +
                    "`Deve [resultado] quando [ação]` de test/",
            )
        }

        @Test
        fun `Deve reportar o teste quando a descrição tem código`(@TempDir dir: File) {
            plantUnitTests(
                dir,
                "Deve devolver erros quando é um ValidationErrors",
                "Deve chamar dao.upsert quando salva",
                "Deve somar(a) quando a = b",
                "Deve usar snake_case quando salva",
            )

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `Deve chamar dao.upsert quando salva` tem código na descrição",
                "Planted.kt: `Deve devolver erros quando é um ValidationErrors` tem código na descrição",
                "Planted.kt: `Deve somar(a) quando a = b` tem código na descrição",
                "Planted.kt: `Deve usar snake_case quando salva` tem código na descrição",
            )
        }

        @Test
        fun `Deve aceitar o teste quando a descrição tem sigla ou termo capitalizado`(@TempDir dir: File) {
            plantUnitTests(dir, "Deve exibir o SCV do Hóspede quando o Lead tem Atendimento")

            val result = TestNamesRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando a condição do nome está vazia`(@TempDir dir: File) {
            plantUnitTests(dir, "Deve salvar Estadia quando ")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `Deve salvar Estadia quando ` foge do template " +
                    "`Deve [resultado] quando [ação]` de test/",
            )
        }

        @Test
        fun `Deve ignorar a função quando ela não é um teste`(@TempDir dir: File) {
            File(dir, "src/test/Planted.kt").also { it.parentFile.mkdirs() }.writeText(
                "package p\n\nclass Planted {\n    fun `nome livre`() {}\n}\n",
            )

            val result = TestNamesRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o nome instrumentado é sem espaços e tem a condição`(@TempDir dir: File) {
            plantInstrumentedTests(dir, "deveSalvarEstadiaQuandoDadosValidos")

            val result = TestNamesRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando o nome instrumentado usa backtick`(@TempDir dir: File) {
            plantInstrumentedTests(dir, "`Deve salvar Estadia quando dados são válidos`")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `Deve salvar Estadia quando dados são válidos` foge do template " +
                    "`deve<Resultado>Quando<Condição>` de androidTest/",
            )
        }

        @Test
        fun `Deve reportar o teste quando o nome instrumentado não tem Quando`(@TempDir dir: File) {
            plantInstrumentedTests(dir, "deveSalvarEstadia")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `deveSalvarEstadia` foge do template " +
                    "`deve<Resultado>Quando<Condição>` de androidTest/",
            )
        }

        @Test
        fun `Deve reportar o teste quando o nome instrumentado termina em Quando`(@TempDir dir: File) {
            plantInstrumentedTests(dir, "deveSalvarQuando")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `deveSalvarQuando` foge do template " +
                    "`deve<Resultado>Quando<Condição>` de androidTest/",
            )
        }

        @Test
        fun `Deve reportar o teste quando o nome instrumentado não separa o resultado de deve`(
            @TempDir dir: File,
        ) {
            plantInstrumentedTests(dir, "devesalvarQuandoValido")

            val result = TestNamesRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Planted.kt: `devesalvarQuandoValido` foge do template " +
                    "`deve<Resultado>Quando<Condição>` de androidTest/",
            )
        }

        @Test
        fun `Deve ignorar o teste quando ele não está nos testes unitários nem nos instrumentados`(@TempDir dir: File) {
            File(dir, "src/main/Planted.kt").also { it.parentFile.mkdirs() }.writeText(
                "package p\n\nclass Planted {\n    @org.junit.jupiter.api.Test\n" +
                    "    fun `qualquer coisa`() {}\n}\n",
            )

            val result = TestNamesRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plantUnitTests(dir: File, vararg names: String) {
        val functions = names.joinToString("\n") { name ->
            "    @org.junit.jupiter.api.Test\n    fun `$name`() {}"
        }
        File(dir, "src/test/Planted.kt").also { it.parentFile.mkdirs() }
            .writeText("package p\n\nclass Planted {\n$functions\n}\n")
    }

    private fun plantInstrumentedTests(dir: File, vararg names: String) {
        val file = File(dir, "src/androidTest/Planted.kt").also { it.parentFile.mkdirs() }
        val functions = names.joinToString("\n") { name ->
            "    @org.junit.Test\n    fun $name() {}"
        }
        file.writeText("package p\n\nclass Planted {\n$functions\n}\n")
    }
}
