package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestFileRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando o arquivo espelha o de produção no mesmo pacote`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }")
            plant(dir, "androidTest", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando não existe arquivo de produção correspondente`(
            @TempDir dir: File,
        ) {
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `Foo.kt` deve existir em main no pacote `a.b`",
            )
        }

        @Test
        fun `Deve reportar o teste quando o pacote difere do arquivo de produção`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b.presentation", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `Foo.kt` deve existir em main no pacote `a.b`",
            )
        }

        @Test
        fun `Deve reportar o teste quando o arquivo não termina em Test`(@TempDir dir: File) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooSpec", "class FooSpec { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooSpec.kt: o nome do arquivo de teste deve terminar em `Test`")
        }

        @Test
        fun `Deve reportar o teste quando o arquivo não declara a classe de mesmo nome`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooTest", "class OutraTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: o arquivo deve declarar a classe `FooTest`")
        }

        @Test
        fun `Deve ignorar o arquivo quando ele não tem teste`(@TempDir dir: File) {
            plant(dir, "test", "a.b", "ViewModelCoroutineTest", "annotation class ViewModelCoroutineTest")

            val result = TestFileRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste de arquitetura quando não existe arquivo de produção correspondente`(
            @TempDir dir: File,
        ) {
            plant(dir, "test", "$ROOT.architecture", "ArchitectureRulesTest", "class ArchitectureRulesTest { $TEST }")
            plant(dir, "test", "$ROOT.architecture.rules", "FooRuleTest", "class FooRuleTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste de arquitetura quando o arquivo foge do nome ou declara mais de uma classe`(
            @TempDir dir: File,
        ) {
            plant(dir, "test", "$ROOT.architecture", "FooSpec", "class FooSpec { $TEST }")
            plant(dir, "test", "$ROOT.architecture", "BarTest", "class OutraTest { $TEST }")
            plant(dir, "test", "$ROOT.architecture", "BazTest", "class BazTest { $TEST }\nclass QuxTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "BarTest.kt: o arquivo deve declarar a classe `BarTest`",
                "BazTest.kt: o arquivo deve declarar uma única classe de teste",
                "FooSpec.kt: o nome do arquivo de teste deve terminar em `Test`",
            )
        }

        @Test
        fun `Deve reportar o teste quando o pacote só começa com o nome do pacote de arquitetura`(
            @TempDir dir: File,
        ) {
            plant(dir, "test", "$ROOT.architectureX", "FooTest", "class FooTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `Foo.kt` deve existir em main no pacote `$ROOT.architectureX`",
            )
        }

        @Test
        fun `Deve reportar o teste quando o arquivo declara mais de uma classe de teste`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(
                dir,
                "test",
                "a.b",
                "FooTest",
                "class FooTest { $TEST }\nclass OutraTest { $TEST }",
            )

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: o arquivo deve declarar uma única classe de teste")
        }

        @Test
        fun `Deve aceitar o teste quando o arquivo tem uma classe de teste e uma auxiliar`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }\nclass Helper")

            val result = TestFileRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste instrumentado quando não existe arquivo de produção correspondente`(
            @TempDir dir: File,
        ) {
            plant(dir, "androidTest", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `Foo.kt` deve existir em main no pacote `a.b`",
            )
        }

        @Test
        fun `Deve reportar o teste quando a classe de mesmo nome só existe aninhada`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "test", "a.b", "FooTest", "class OutraTest { class FooTest { $TEST } }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: o arquivo deve declarar a classe `FooTest`")
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "androidTest", "a.b", "ZetaTest", "class ZetaTest { $TEST }")
            plant(dir, "test", "a.b", "AlfaTest", "class AlfaTest { $TEST }")

            val result = TestFileRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AlfaTest.kt: `Alfa.kt` deve existir em main no pacote `a.b`",
                "ZetaTest.kt: `Zeta.kt` deve existir em main no pacote `a.b`",
            )
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, sourceSet: String, pkg: String, fileName: String, body: String) {
        File(dir, "src/$sourceSet/${pkg.replace('.', '/')}/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n$body\n")
    }

    private companion object {
        const val ROOT = "com.fstengineering.leasilycrm"
        const val TEST = "@org.junit.Test fun deveFazerQuandoPuder() {}"
    }
}
