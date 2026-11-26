package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TestDaoRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve reportar o teste de unidade quando o SUT é uma classe Dao`(@TempDir dir: File) {
            plant(dir, "main", "Foo", "@androidx.room.Dao abstract class Foo")
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `Foo` é @Dao; só se testa em androidTest")
        }

        @Test
        fun `Deve reportar o teste de unidade quando o SUT é uma interface Dao`(@TempDir dir: File) {
            plant(dir, "main", "Foo", "@androidx.room.Dao interface Foo")
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `Foo` é @Dao; só se testa em androidTest")
        }

        @Test
        fun `Deve aceitar o teste instrumentado quando o SUT é um Dao`(@TempDir dir: File) {
            plant(dir, "main", "Foo", "@androidx.room.Dao abstract class Foo")
            plant(dir, "androidTest", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste de unidade quando o SUT não é um Dao`(@TempDir dir: File) {
            plant(dir, "main", "Foo", "class Foo")
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste de unidade quando o Dao está aninhado no arquivo do SUT`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "Foo", "class Foo { @androidx.room.Dao interface Inner }")
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `Foo` é @Dao; só se testa em androidTest")
        }

        @Test
        fun `Deve reportar o teste de unidade quando o Dao é uma classe aninhada no arquivo do SUT`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "Foo", "class Foo { @androidx.room.Dao abstract class Inner }")
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooTest.kt: `Foo` é @Dao; só se testa em androidTest")
        }

        @Test
        fun `Deve ignorar o arquivo quando ele não tem testes`(@TempDir dir: File) {
            plant(dir, "main", "Foo", "@androidx.room.Dao abstract class Foo")
            plant(dir, "test", "FooTest", "class FooTest")

            val result = TestDaoRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste quando o SUT não existe em produção`(@TempDir dir: File) {
            plant(dir, "test", "FooTest", "class FooTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "main", "Zeta", "@androidx.room.Dao abstract class Zeta")
            plant(dir, "main", "Alfa", "@androidx.room.Dao abstract class Alfa")
            plant(dir, "test", "ZetaTest", "class ZetaTest { $TEST }")
            plant(dir, "test", "AlfaTest", "class AlfaTest { $TEST }")

            val result = TestDaoRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AlfaTest.kt: `Alfa` é @Dao; só se testa em androidTest",
                "ZetaTest.kt: `Zeta` é @Dao; só se testa em androidTest",
            )
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(dir: File, sourceSet: String, fileName: String, body: String) {
        File(dir, "src/$sourceSet/a/b/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package a.b\n\n$body\n")
    }

    private companion object {
        const val TEST = "@org.junit.Test fun deveFazerQuandoPuder() {}"
    }
}
