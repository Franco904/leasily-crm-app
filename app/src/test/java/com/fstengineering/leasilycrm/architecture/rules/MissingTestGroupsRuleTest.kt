package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class MissingTestGroupsRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve reportar o método público quando ele não tem grupo`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {}\n fun baz() {} }")
            plantTest(dir, group("bar", "BarTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(missing("baz", "BazTests"))
        }

        @Test
        fun `Deve aceitar o teste quando cada método público tem um grupo`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {}\n fun bar(x: Int) {}\n fun baz() {} }")
            plantTest(dir, group("bar", "BarTests") + group("baz", "BazTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o método quando ele sobrescreve outro sem declarar visibilidade`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo : Base() { override fun bar() {} }")
            plantTest(dir, group("outro", "OutroTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(missing("bar", "BarTests"))
        }

        @Test
        fun `Deve reportar o método quando ele sobrescreve outro declarando proteção`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo : Base() { protected override fun bar() {} }")
            plantTest(dir, group("outro", "OutroTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(missing("bar", "BarTests"))
        }

        @Test
        fun `Deve aceitar o teste quando o método sobrescrito é restrito ao módulo`(
            @TempDir dir: File,
        ) {
            plantSubject(
                dir,
                "class Foo : Base() { internal override fun interno() {}\n" +
                    " private class Oculta : Base() { override fun segredo() {} }\n fun bar() {} }",
            )
            plantTest(dir, group("bar", "BarTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o método não é público`(@TempDir dir: File) {
            plantSubject(
                dir,
                "class Foo {\n private fun oculto() {}\n internal fun interno() {}\n" +
                    " protected fun herdavel() {}\n private class Oculta { fun segredo() {} }\n" +
                    " fun bar() { fun local() {} } }",
            )
            plantTest(dir, group("bar", "BarTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando o membro público sem grupo é uma propriedade`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { val valor: Int = 1\n fun bar() {} }")
            plantTest(dir, group("bar", "BarTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o método quando o grupo dele não tem teste`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {}\n fun baz() {} }")
            plantTest(
                dir,
                group("bar", "BarTests") +
                    "@org.junit.jupiter.api.Nested\n" +
                    "@org.junit.jupiter.api.DisplayName(\"baz\")\n" +
                    "inner class BazTests",
            )

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(missing("baz", "BazTests"))
        }

        @Test
        fun `Deve reportar o método quando o grupo dele não é Nested`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(
                dir,
                "@org.junit.jupiter.api.DisplayName(\"bar\")\ninner class BarTests { $TEST }",
            )

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(missing("bar", "BarTests"))
        }

        @Test
        fun `Deve ignorar o teste de arquitetura quando existe produção de mesmo nome no pacote`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "$ROOT.architecture", "Foo", "class Foo { fun bar() {} }")
            plant(dir, "test", "$ROOT.architecture", "FooTest", "class FooTest { $TEST }")

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste instrumentado quando ele não está agrupado por método`(
            @TempDir dir: File,
        ) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plant(dir, "androidTest", "a.b", "FooTest", "class FooTest { $TEST }")

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o SUT quando o arquivo de teste não tem teste`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun bar() {} }")
            plantTest(dir, "")

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste quando o SUT não existe em produção`(@TempDir dir: File) {
            plantTest(dir, group("bar", "BarTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plantSubject(dir, "class Foo { fun zeta() {}\n fun alfa() {} }")
            plantTest(dir, group("outro", "OutroTests"))

            val result = MissingTestGroupsRule.violations(scopeOf(dir))

            result shouldBeEqualTo
                listOf(missing("alfa", "AlfaTests"), missing("zeta", "ZetaTests"))
        }
    }

    private fun missing(method: String, group: String) =
        "FooTest.kt: o método público `$method` do SUT deve ter um grupo `@Nested` `$group` " +
            "com teste"

    private fun group(display: String, name: String) =
        "@org.junit.jupiter.api.Nested\n" +
            "@org.junit.jupiter.api.DisplayName(\"$display\")\n" +
            "inner class $name { $TEST }\n"

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
