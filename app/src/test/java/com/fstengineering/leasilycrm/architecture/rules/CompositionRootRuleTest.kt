package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class CompositionRootRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar a raiz de composição quando cada membro importa o que o papel permite`(
            @TempDir dir: File,
        ) {
            plantRootPackage(dir)
            plant(
                dir, "main", "$ROOT.core.di", "DependencyInjection",
                imports = listOf(
                    "$ROOT.imoveis.di.imoveisViewModelModule",
                    "$ROOT.core.di.modules.daoModule",
                    "org.koin.core.module.Module",
                ),
            )
            plant(dir, "main", "$ROOT.imoveis.infra", "ImoveisReadDataSource", body = DAO_CLASS)
            plant(
                dir, "main", "$ROOT.core.infra.database", "AppDatabase",
                imports = listOf(
                    "$ROOT.imoveis.infra.ImoveisReadDataSource",
                    "$ROOT.core.infra.database.entities.ImovelEntity",
                ),
                body = DATABASE_CLASS,
            )

            val result = CompositionRootRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o arquivo do pacote raiz quando ele não é membro da raiz de composição`(
            @TempDir dir: File,
        ) {
            plantRootPackage(dir)
            plant(dir, "main", ROOT, "Utils")

            val result = CompositionRootRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Utils.kt: $ROOT_PACKAGE_MESSAGE",
            )
        }

        @Test
        fun `Deve reportar o core di quando importa um módulo fora do pacote di dele`(
            @TempDir dir: File,
        ) {
            plant(
                dir, "main", "$ROOT.core.di.modules", "FooModule",
                imports = listOf(
                    "$ROOT.imoveis.presentation.ImoveisViewModel",
                    "$ROOT.imoveis.diretorio.Bar",
                ),
            )

            val result = CompositionRootRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "FooModule.kt: $CORE_DI_MESSAGE `$ROOT.imoveis.diretorio.Bar`",
                "FooModule.kt: $CORE_DI_MESSAGE `$ROOT.imoveis.presentation.ImoveisViewModel`",
            )
        }

        @Test
        fun `Deve reportar a classe Database quando importa de um módulo algo que não é Dao`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "$ROOT.imoveis.infra", "ImovelEntity")
            plant(
                dir, "main", "$ROOT.core.infra.database", "AppDatabase",
                imports = listOf("$ROOT.imoveis.infra.ImovelEntity"),
                body = DATABASE_CLASS,
            )

            val result = CompositionRootRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "AppDatabase.kt: a classe @Database deve importar dos módulos só classes @Dao, " +
                    "não `$ROOT.imoveis.infra.ImovelEntity`",
            )
        }

        @Test
        fun `Deve reportar as classes Database quando há mais de uma`(@TempDir dir: File) {
            plant(dir, "main", "$ROOT.core.infra.database", "AppDatabase", body = DATABASE_CLASS)
            plant(
                dir, "main", "$ROOT.imoveis.infra", "ImoveisDatabase",
                body = "@androidx.room.Database abstract class ImoveisDatabase",
            )

            val result = CompositionRootRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "A classe @Database deve ser única: AppDatabase.kt, ImoveisDatabase.kt",
            )
        }

        @Test
        fun `Deve aceitar o teste do core di e a classe Database de teste quando importam a infra de teste`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", "$ROOT.core.infra.database", "AppDatabase", body = DATABASE_CLASS)
            plant(
                dir, "test", "$ROOT.core.di", "DependencyInjectionTest",
                imports = listOf("$ROOT.testUtils.annotations.ViewModelCoroutineTest"),
            )
            plant(
                dir, "androidTest", "$ROOT.core.infra.database", "FixtureDatabase",
                imports = listOf("$ROOT.testUtils.fakes.saveFakeCliente"),
                body = "@androidx.room.Database abstract class FixtureDatabase",
            )

            val result = CompositionRootRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve aceitar o arquivo do pacote raiz quando ele é de teste`(@TempDir dir: File) {
            plant(dir, "test", ROOT, "DestinationHandlingTest")

            val result = CompositionRootRule.violations(scopeOf(dir))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            plant(dir, "main", ROOT, "Zeta")
            plant(dir, "main", ROOT, "Alfa")

            val result = CompositionRootRule.violations(scopeOf(dir))

            result shouldBeEqualTo listOf(
                "Alfa.kt: $ROOT_PACKAGE_MESSAGE",
                "Zeta.kt: $ROOT_PACKAGE_MESSAGE",
            )
        }
    }

    @Nested
    @DisplayName("isMember")
    inner class IsMemberTests {
        @Test
        fun `Deve incluir o arquivo na raiz de composição quando ele é do pacote raiz, do core di ou da classe Database`(
            @TempDir dir: File,
        ) {
            plantRootPackage(dir)
            plant(dir, "main", "$ROOT.core.di", "DependencyInjection")
            plant(dir, "main", "$ROOT.core.di.modules", "DaoModule")
            plant(dir, "main", "$ROOT.core.infra.database", "AppDatabase", body = DATABASE_CLASS)

            val result = scopeOf(dir).files.filter { CompositionRootRule.isMember(it) }

            result.map { it.nameWithExtension }.sorted() shouldBeEqualTo listOf(
                "AppDatabase.kt",
                "DaoModule.kt",
                "DependencyInjection.kt",
                "DestinationHandling.kt",
                "Destinations.kt",
                "LeasilyCrmApplication.kt",
                "MainActivity.kt",
            )
        }

        @Test
        fun `Deve deixar o arquivo fora da raiz de composição quando ele é do restante do core e do pacote raiz`(
            @TempDir dir: File,
        ) {
            plant(dir, "main", ROOT, "Utils")
            plant(dir, "test", ROOT, "MainActivity")
            plant(dir, "main", "$ROOT.core.infra.database", "Converters")
            plant(dir, "main", "$ROOT.core.dir", "Foo")
            plant(dir, "main", "$ROOT.imoveis.di", "ImoveisModule")
            plant(dir, "test", "$ROOT.core.di", "DependencyInjectionTest")
            plant(
                dir, "androidTest", "$ROOT.core.infra.database", "FixtureDatabase",
                body = "@androidx.room.Database abstract class FixtureDatabase",
            )

            val result = scopeOf(dir).files.filter { CompositionRootRule.isMember(it) }

            result.shouldBeEmpty()
        }
    }

    private fun plantRootPackage(dir: File) {
        listOf("DestinationHandling", "Destinations", "MainActivity", "LeasilyCrmApplication")
            .forEach {
                plant(dir, "main", ROOT, it, imports = listOf("$ROOT.clientes.presentation.Bar"))
            }
    }

    private fun scopeOf(dir: File) = Konsist.scopeFromExternalDirectory(dir.absolutePath)

    private fun plant(
        dir: File,
        sourceSet: String,
        pkg: String,
        fileName: String,
        imports: List<String> = emptyList(),
        body: String = "class $fileName",
    ) {
        File(dir, "src/$sourceSet/${pkg.replace('.', '/')}/$fileName.kt")
            .also { it.parentFile.mkdirs() }
            .writeText("package $pkg\n\n${imports.joinToString("\n") { "import $it" }}\n\n$body\n")
    }

    private companion object {
        const val ROOT = "com.fstengineering.leasilycrm"
        const val DAO_CLASS = "@androidx.room.Dao abstract class ImoveisReadDataSource"
        const val DATABASE_CLASS = "@androidx.room.Database abstract class AppDatabase"
        const val ROOT_PACKAGE_MESSAGE = "o pacote raiz deve ter só a raiz de composição " +
            "(DestinationHandling.kt, Destinations.kt, LeasilyCrmApplication.kt, MainActivity.kt)"
        const val CORE_DI_MESSAGE = "`core/di` deve importar dos módulos só pacotes `.di`, não"
    }
}
