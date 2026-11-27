package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ContextBoundaryRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o módulo quando importa o próprio módulo, o mesmo contexto, o core e as classes permitidas do pacote raiz`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf(
                    "$ROOT.imoveis.domain.Bar",
                    "$ROOT.imovelRegistration.domain.entities.Imovel",
                    "$ROOT.core.domain.utils.AppResult",
                    "$ROOT.Destinations",
                    "$ROOT.R",
                    "$ROOT.BuildConfig",
                    "androidx.compose.ui.Modifier",
                ),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o módulo quando importa um módulo de outro contexto`(@TempDir dir: File) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf("$ROOT.clientes.domain.Cliente"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Foo.kt", "imoveis", "$ROOT.clientes.domain.Cliente"),
            )
        }

        @Test
        fun `Deve reportar o core quando importa um módulo`(@TempDir dir: File) {
            plant(
                dir, "main", "$ROOT.core.presentation", "Foo",
                imports = listOf("$ROOT.imoveis.presentation.Bar"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Foo.kt", "core", "$ROOT.imoveis.presentation.Bar"),
            )
        }

        @Test
        fun `Deve reportar o módulo sem contexto quando importa outro módulo sem contexto`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.home.presentation", "Foo",
                imports = listOf("$ROOT.home.domain.Bar", "$ROOT.settings.presentation.Baz"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Foo.kt", "home", "$ROOT.settings.presentation.Baz"),
            )
        }

        @Test
        fun `Deve reportar o módulo quando importa do pacote raiz uma classe ou função fora das permitidas`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf("$ROOT.MainActivity", "$ROOT.handleDestination"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Foo.kt", "imoveis", "$ROOT.MainActivity"),
                message("Foo.kt", "imoveis", "$ROOT.handleDestination"),
            )
        }

        @Test
        fun `Deve reportar o módulo quando importa um pacote que não está na tabela`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf("$ROOT.inventado.Bar"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(message("Foo.kt", "imoveis", "$ROOT.inventado.Bar"))
        }

        @Test
        fun `Deve aceitar a raiz de composição quando importa módulos de qualquer contexto`(
            @TempDir dir: File,
        ) {
            val imports =
                listOf("$ROOT.imoveis.presentation.Bar", "$ROOT.clientes.presentation.Baz")
            plant(dir, "main", ROOT, "DestinationHandling", imports = imports)
            plant(dir, "main", "$ROOT.core.di", "DependencyInjection", imports = imports)
            plant(
                dir, "main", "$ROOT.core.infra.database", "AppDatabase", imports = imports,
                body = "@androidx.room.Database abstract class AppDatabase",
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o teste quando importa um módulo de outro contexto`(@TempDir dir: File) {
            val imports = listOf("$ROOT.clientes.domain.Cliente")
            plant(dir, "test", "$ROOT.imoveis.presentation", "FooTest", imports = imports)
            plant(dir, "androidTest", "$ROOT.imoveis.infra", "BarTest", imports = imports)

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("BarTest.kt", "imoveis", "$ROOT.clientes.domain.Cliente"),
                message("FooTest.kt", "imoveis", "$ROOT.clientes.domain.Cliente"),
            )
        }

        @Test
        fun `Deve aceitar o teste quando importa a infra de teste`(@TempDir dir: File) {
            plant(
                dir, "androidTest", "$ROOT.imoveis.infra", "FooTest",
                imports = listOf(
                    "$ROOT.testUtils.IntegrationTest",
                    "$ROOT.testUtils.fakes.saveFakeCliente",
                ),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar a infra de teste quando importa módulos de qualquer contexto`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "androidTest", "$ROOT.testUtils.fakes", "ClienteFakes",
                imports = listOf("$ROOT.clientes.domain.Cliente", "$ROOT.imoveis.domain.Imovel"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o código de produção quando importa um pacote chamado como a infra de teste`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf("$ROOT.testUtils.Bar"),
            )
            plant(dir, "main", "$ROOT.testUtils", "Bar", imports = listOf("$ROOT.imoveis.Baz"))

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Bar.kt", "testUtils", "$ROOT.imoveis.Baz"),
                message("Foo.kt", "imoveis", "$ROOT.testUtils.Bar"),
            )
        }

        @Test
        fun `Deve ignorar o arquivo e o import quando estão fora do pacote raiz ou só começam com o nome dele`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.imoveis.presentation", "Foo",
                imports = listOf(
                    "com.fstengineering.leasilycrmfoo.clientes.Bar",
                    "org.exemplo.clientes.Baz",
                ),
            )
            plant(dir, "main", "org.exemplo", "Qux", imports = listOf("$ROOT.clientes.Bar"))
            plant(
                dir, "main", "com.fstengineering.leasilycrmfoo.imoveis", "Quux",
                imports = listOf("$ROOT.clientes.Bar"),
            )

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "main", "$ROOT.imoveis", "Zeta", imports = listOf("$ROOT.clientes.Bar"))
            plant(dir, "main", "$ROOT.clientes", "Alfa", imports = listOf("$ROOT.imoveis.Baz"))

            val result = ContextBoundaryRule.violations(scopeOf(dir), CONTEXTS)

            result shouldBeEqualTo listOf(
                message("Alfa.kt", "clientes", "$ROOT.imoveis.Baz"),
                message("Zeta.kt", "imoveis", "$ROOT.clientes.Bar"),
            )
        }
    }

    private fun message(file: String, module: String, import: String) =
        "$file: `$module` não deve importar `$import` " +
            "(só o próprio contexto, o `core` e, do pacote raiz, `Destinations`)"

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(
        dir: File,
        sourceSet: String,
        pkg: String,
        fileName: String,
        imports: List<String>,
        body: String = "class $fileName",
    ) {
        File(dir, "src/$sourceSet/${pkg.replace('.', '/')}/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n${imports.joinToString("\n") { "import $it" }}\n\n$body\n")
    }

    private companion object {
        const val ROOT = "com.fstengineering.leasilycrm"

        val CONTEXTS = mapOf(
            "core" to "Compartilhado",
            "home" to null,
            "settings" to null,
            "imoveis" to "Imóveis",
            "imovelRegistration" to "Imóveis",
            "clientes" to "Clientes",
        )
    }
}
