package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestGroupsRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando cada grupo nomeia um método público do SUT`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {}\n fun baz() {} }")
            plantTest(dir, group("bar", "BarTests") + group("baz", "BazTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o método é público por omissão de visibilidade`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { public fun bar() {} }")
            plantTest(dir, group("bar", "BarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o método é de um companion ou de uma classe aninhada`(
            @TempDir dir: File,
        ) {
            plantSubject(
                dir,
                "class Foo {\n companion object { fun parse() {} }\n class Inner { fun build() {} }\n}",
            )
            plantTest(dir, group("parse", "ParseTests") + group("build", "BuildTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o grupo nomeia um método sobrescrito com proteção`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo : Base() { protected override fun bar() {} }")
            plantTest(dir, group("bar", "BarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o grupo nomeia uma propriedade pública do SUT`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "object Foo { val bar: Int = 1 }")
            plantTest(dir, group("bar", "BarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o método é uma função top-level do arquivo`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "fun String.toBar(): String = this")
            plantTest(dir, group("toBar", "ToBarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o grupo quando o método está numa classe não pública do SUT`(
            @TempDir dir: File,
        ) {
            plantSubject(
                dir,
                "class Foo { fun bar() {}\n private class Oculta { fun segredo() {} }\n" +
                    " internal class Interna { fun interno() {} } }",
            )
            plantTest(
                dir,
                group("bar", "BarTests") +
                    group("segredo", "SegredoTests") +
                    group("interno", "InternoTests"),
            )

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `@DisplayName(\"interno\")` deve nomear um método ou propriedade pública do SUT",
                "FooTest.kt: `@DisplayName(\"segredo\")` deve nomear um método ou propriedade pública do SUT",
            )
        }

        @Test
        fun `Deve aceitar o teste quando o método tem sobrecargas`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {}\n fun bar(x: Int) {} }")
            plantTest(dir, group("bar", "BarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando ele está solto na classe de teste`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, "@org.junit.Test fun solto() {}")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `solto` deve estar numa classe `@Nested` de método público do SUT",
            )
        }

        @Test
        fun `Deve reportar o grupo quando ele tem testes e não é Nested`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, "@org.junit.jupiter.api.DisplayName(\"bar\")\ninner class BarTests { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `BarTests` tem testes e deve ser `@Nested`")
        }

        @Test
        fun `Deve reportar o grupo quando ele não tem nome de exibição`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, "@org.junit.jupiter.api.Nested\ninner class BarTests { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `BarTests` deve ter `@DisplayName` com o nome do método",
            )
        }

        @Test
        fun `Deve reportar o grupo quando o nome de exibição não é membro público do SUT`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {}\n private fun oculto() {} }")
            plantTest(
                dir,
                group("inexistente", "InexistenteTests") +
                    group("oculto", "OcultoTests") +
                    group("Foo.bar", "FooBarTests") +
                    group("bar (caso feliz)", "BarTests"),
            )

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `@DisplayName(\"Foo.bar\")` deve nomear um método ou propriedade pública do SUT",
                "FooTest.kt: `@DisplayName(\"bar (caso feliz)\")` deve nomear um método ou propriedade pública do SUT",
                "FooTest.kt: `@DisplayName(\"inexistente\")` deve nomear um método ou propriedade pública do SUT",
                "FooTest.kt: `@DisplayName(\"oculto\")` deve nomear um método ou propriedade pública do SUT",
            )
        }

        @Test
        fun `Deve reportar o grupo quando o nome da classe não é o método seguido de Tests`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, group("bar", "CasosDoBar"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `CasosDoBar` deve se chamar `BarTests`")
        }

        @Test
        fun `Deve reportar o grupo quando há mais de um para o mesmo método`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, group("bar", "BarTests") + group("bar", "OutroBarTests"))

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: `OutroBarTests` deve se chamar `BarTests`",
                "FooTest.kt: o método `bar` deve ter um único grupo `@Nested`",
            )
        }

        @Test
        fun `Deve reportar o grupo quando ele aninha outra classe de teste`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(
                dir,
                group("bar", "BarTests", "inner class Fundo { $TEST }"),
            )

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `BarTests` não deve aninhar outra classe de teste")
        }

        @Test
        fun `Deve ignorar o teste de arquitetura quando ele não tem SUT em produção`(
            @TempDir dir: File,
        ) {
            plant(dir, "test", "$ROOT.architecture", "FooRuleTest", "class FooRuleTest { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando a classe de teste tem função auxiliar fora dos grupos`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, group("bar", "BarTests") + "private fun createSut() = Unit")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o grupo quando a classe de teste está aninhada em dois níveis`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(
                dir,
                group("bar", "BarTests", "inner class Fundo { inner class Mais { $TEST } }"),
            )

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `BarTests` não deve aninhar outra classe de teste")
        }

        @Test
        fun `Deve ignorar o teste de arquitetura quando existe produção de mesmo nome no pacote`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "$ROOT.architecture", "Foo", "class Foo { fun bar() {} }")
            plant(dir, "test", "$ROOT.architecture", "FooTest", "class FooTest { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste instrumentado quando ele não está agrupado por método`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "a.b", "Foo", "class Foo { fun bar() {} }")
            plant(dir, "androidTest", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste quando o SUT não existe em produção`(@TempDir dir: File) {
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "main", "a.b", "Foo", "class Foo")
            plant(dir, "main", "a.b", "Alfa", "class Alfa")
            plant(dir, "test", "a.b", "FooTest", "class FooTest { $TEST }")
            plant(dir, "test", "a.b", "AlfaTest", "class AlfaTest { $TEST }")

            val result = TestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AlfaTest.kt: `deveFazerQuandoPuder` deve estar numa classe `@Nested` de método público do SUT",
                "FooTest.kt: `deveFazerQuandoPuder` deve estar numa classe `@Nested` de método público do SUT",
            )
        }
    }

    private fun group(display: String, name: String, extra: String = "") =
        "@org.junit.jupiter.api.Nested\n" +
            "@org.junit.jupiter.api.DisplayName(\"$display\")\n" +
            "inner class $name { $TEST $extra }\n"

    private fun plantSubject(dir: File, body: String) = plant(dir, "main", "a.b", "Foo", body)

    private fun plantTest(dir: File, body: String) =
        plant(dir, "test", "a.b", "FooTest", "class FooTest {\n$body\n}")

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
