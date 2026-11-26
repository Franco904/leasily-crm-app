package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class UnitTestApisRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando todo mock dispensa só o stub de Unit`(@TempDir dir: File) {
            plantTest(
                dir,
                """
                val a: Dep = mockk(relaxUnitFun = true)
                val b = mockk<Dep>(relaxUnitFun = true)
                val c = mockk<Map<String, List<Int>>>(relaxUnitFun = true)
                val d = mockk<Dep>(
                    relaxUnitFun = true,
                )
                val e = mockk<Dep>(name = nome(), relaxUnitFun = true)
                val f = mockk<Dep> (relaxUnitFun = true)
                val g: Dep = mockk (relaxUnitFun = true)
                """,
            )

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste quando um identificador apenas contém o nome da API`(
            @TempDir dir: File,
        ) {
            plantTest(
                dir,
                """
                val notrunBlocking = 1
                val notrelaxed = 2
                val notmockk = 3
                """,
            )

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando um mock é criado sem dispensar o stub de Unit`(@TempDir dir: File) {
            plantTest(
                dir,
                """
                val a: Dep = mockk(relaxUnitFun = true)
                val b: Dep = mockk()
                """,
            )

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: toda criação de mock deve levar `relaxUnitFun = true`")
        }

        @Test
        fun `Deve reportar o teste quando o mock tipado ou com bloco não dispensa o stub de Unit`(
            @TempDir dir: File,
        ) {
            plantTest(dir, "val a = mockk<Map<String, List<Int>>>()")
            plantTest(dir, "val a = mockk<Dep> { }", name = "BarTest")
            plantTest(dir, "val a = mockk<Dep>(name = \"x\")", name = "BazTest")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "BarTest.kt: toda criação de mock deve levar `relaxUnitFun = true`",
                "BazTest.kt: toda criação de mock deve levar `relaxUnitFun = true`",
                "FooTest.kt: toda criação de mock deve levar `relaxUnitFun = true`",
            )
        }

        @Test
        fun `Deve reportar o teste quando um mock é relaxado`(@TempDir dir: File) {
            plantTest(dir, "val a: Dep = mockk(relaxed = true, relaxUnitFun = true)")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: mock não deve usar `relaxed = true`; use `mockk(relaxUnitFun = true)`",
            )
        }

        @Test
        fun `Deve reportar o teste quando usa bloqueio de coroutine`(@TempDir dir: File) {
            plantTest(dir, "fun f() = runBlocking { }")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: teste de unidade não deve usar `runBlocking`; use `runTest`")
        }

        @Test
        fun `Deve ignorar o import do mockk quando ele não é uma criação de mock`(@TempDir dir: File) {
            plantTest(dir, "val a: Dep = mockk(relaxUnitFun = true)", imports = "import io.mockk.mockk")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar a API quando ela aparece só em comentário ou texto`(@TempDir dir: File) {
            plantTest(
                dir,
                "// runBlocking e mockk() e relaxed = true\nval t = \"runBlocking { mockk() } relaxed = true\"",
            )

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar a API quando ela aparece só no nome de uma função entre crases`(
            @TempDir dir: File,
        ) {
            plantTest(dir, "fun `Deve evitar runBlocking e mockk() e relaxed = true`() {}")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste instrumentado quando ele usa bloqueio de coroutine`(@TempDir dir: File) {
            plantTest(dir, "fun f() = runBlocking { }", sourceSet = "androidTest")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aplicar a regra ao teste de arquitetura quando ele usa bloqueio de coroutine`(@TempDir dir: File) {
            plantTest(dir, "fun f() = runBlocking { }", pkg = "$ROOT.architecture.rules")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: teste de unidade não deve usar `runBlocking`; use `runTest`")
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plantTest(dir, "fun f() = runBlocking { }", name = "ZetaTest")
            plantTest(dir, "fun f() = runBlocking { }", name = "AlfaTest")

            val result = UnitTestApisRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AlfaTest.kt: teste de unidade não deve usar `runBlocking`; use `runTest`",
                "ZetaTest.kt: teste de unidade não deve usar `runBlocking`; use `runTest`",
            )
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plantTest(
        dir: File,
        body: String,
        name: String = "FooTest",
        sourceSet: String = "test",
        pkg: String = "a.b",
        imports: String = "",
    ) {
        File(dir, "src/$sourceSet/${pkg.replace('.', '/')}/$name.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n$imports\n\nclass $name {\n${body.trimIndent()}\n}\n")
    }

    private companion object {
        const val ROOT = "com.fstengineering.leasilycrm"
    }
}
