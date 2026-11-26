package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class UnitTestImportsRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o teste quando só importa JUnit 5, kluent e código de produção`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.domain.valueObjects", "FooTest",
                imports = listOf(
                    "org.junit.jupiter.api.Test",
                    "org.junit.jupiter.api.Nested",
                    "org.amshove.kluent.shouldBeEqualTo",
                    "$ROOT.core.domain.utils.AppResult",
                ),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste de domínio quando importa android, androidx ou mockk`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.domain.valueObjects", "FooTest",
                imports = listOf("android.net.Uri", "androidx.compose.ui.Modifier", "io.mockk.mockk"),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: teste de domínio não deve importar `android.net.Uri` (android, androidx, mockk, infra, application, presentation)",
                "FooTest.kt: teste de domínio não deve importar `androidx.compose.ui.Modifier` (android, androidx, mockk, infra, application, presentation)",
                "FooTest.kt: teste de domínio não deve importar `io.mockk.mockk` (android, androidx, mockk, infra, application, presentation)",
            )
        }

        @Test
        fun `Deve reportar o teste de domínio quando importa as camadas infra, application ou presentation`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.domain.valueObjects", "FooTest",
                imports = listOf(
                    "$ROOT.imovelRegistration.infra.mappers.toReadModel",
                    "$ROOT.core.application.models.Bar",
                    "$ROOT.core.presentation.models.Baz",
                    "$ROOT.core.domain.utils.AppResult",
                ),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: teste de domínio não deve importar `$ROOT.core.application.models.Bar` (android, androidx, mockk, infra, application, presentation)",
                "FooTest.kt: teste de domínio não deve importar `$ROOT.core.presentation.models.Baz` (android, androidx, mockk, infra, application, presentation)",
                "FooTest.kt: teste de domínio não deve importar `$ROOT.imovelRegistration.infra.mappers.toReadModel` (android, androidx, mockk, infra, application, presentation)",
            )
        }

        @Test
        fun `Deve aceitar o teste fora do domínio quando importa mockk e as camadas infra e application`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.application.useCases", "FooTest",
                imports = listOf("io.mockk.mockk", "$ROOT.core.infra.utils.onDatabase"),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o teste de domínio quando importa biblioteca externa com pacote infra ou application`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.domain.valueObjects", "FooTest",
                imports = listOf("kotlinx.collections.immutable.persistentListOf", "com.externa.infra.Bar", "org.x.application.Baz"),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando importa Room ou o banco do app`(@TempDir dir: File) {
            plant(
                dir, "$ROOT.core.infra.utils", "FooTest",
                imports = listOf("androidx.room.Room", "$ROOT.core.infra.dataSources.database.AppDatabase"),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: teste de unidade não deve importar `androidx.room.Room` (banco só em androidTest)",
                "FooTest.kt: teste de unidade não deve importar `$ROOT.core.infra.dataSources.database.AppDatabase` (banco só em androidTest)",
            )
        }

        @Test
        fun `Deve reportar o teste quando importa asserção fora do kluent`(@TempDir dir: File) {
            plant(
                dir, "$ROOT.core.utils", "FooTest",
                imports = listOf(
                    "org.junit.jupiter.api.Assertions",
                    "org.junit.jupiter.api.assertThrows",
                    "org.junit.Assert",
                    "kotlin.test.assertEquals",
                    "org.hamcrest.MatcherAssert",
                    "org.assertj.core.api.Assertions",
                    "com.google.common.truth.Truth",
                ),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: teste de unidade não deve importar `com.google.common.truth.Truth` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `kotlin.test.assertEquals` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.assertj.core.api.Assertions` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.hamcrest.MatcherAssert` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.junit.Assert` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.junit.jupiter.api.Assertions` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.junit.jupiter.api.assertThrows` (só JUnit 5 e kluent)",
            )
        }

        @Test
        fun `Deve reportar o teste quando importa JUnit 4`(@TempDir dir: File) {
            plant(dir, "$ROOT.core.utils", "FooTest", imports = listOf("org.junit.Test", "org.junit.runner.RunWith"))

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooTest.kt: teste de unidade não deve importar `org.junit.Test` (só JUnit 5 e kluent)",
                "FooTest.kt: teste de unidade não deve importar `org.junit.runner.RunWith` (só JUnit 5 e kluent)",
            )
        }

        @Test
        fun `Deve aceitar o teste quando importa extensões do JUnit 5 que não são asserções`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.utils", "FooTest",
                imports = listOf(
                    "org.junit.jupiter.api.io.TempDir",
                    "org.junit.jupiter.api.extension.ExtendWith",
                    "org.junit.jupiter.api.BeforeEach",
                ),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o teste instrumentado quando ele importa JUnit 4 e Room`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "$ROOT.core.infra", "FooTest", sourceSet = "androidTest",
                imports = listOf("org.junit.Test", "androidx.room.Room"),
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aplicar a regra ao teste de arquitetura quando ele importa asserção fora do kluent`(
            @TempDir dir: File,
        ) {
            plant(dir, "$ROOT.architecture.rules", "FooRuleTest", imports = listOf("kotlin.test.assertTrue"))

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf("FooRuleTest.kt: teste de unidade não deve importar `kotlin.test.assertTrue` (só JUnit 5 e kluent)")
        }

        @Test
        fun `Deve ignorar o import quando ele aparece só em comentário ou texto`(@TempDir dir: File) {
            plant(
                dir, "$ROOT.core.domain.utils", "FooTest", imports = emptyList(),
                body = "// import android.net.Uri\nval text = \"import org.junit.Assert\"",
            )

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "$ROOT.core.utils", "ZetaTest", imports = listOf("org.junit.Test"))
            plant(dir, "$ROOT.core.utils", "AlfaTest", imports = listOf("kotlin.test.assertTrue"))

            val result = UnitTestImportsRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AlfaTest.kt: teste de unidade não deve importar `kotlin.test.assertTrue` (só JUnit 5 e kluent)",
                "ZetaTest.kt: teste de unidade não deve importar `org.junit.Test` (só JUnit 5 e kluent)",
            )
        }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(
        dir: File,
        pkg: String,
        fileName: String,
        imports: List<String> = emptyList(),
        sourceSet: String = "test",
        body: String = "class $fileName",
    ) {
        File(dir, "src/$sourceSet/${pkg.replace('.', '/')}/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n${imports.joinToString("\n") { "import $it" }}\n\n$body\n")
    }

    private companion object {
        const val ROOT = "com.fstengineering.leasilycrm"
    }
}
