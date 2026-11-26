package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.container.KoScope
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ModulePackagesRuleTest {
    @Nested
    @DisplayName("violations")
    inner class ViolationsTests {
        @Test
        fun `Deve aceitar o escopo quando todos os pacotes constam na tabela`(@TempDir dir: File) {
            val scope = scopeWithPackages(dir, "core.domain", "imoveis.presentation")

            val result =
                ModulePackagesRule.violations(scope, documentedModules = setOf("core", "imoveis"))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar o pacote quando ele não consta na tabela de módulos`(@TempDir dir: File) {
            val scope = scopeWithPackages(dir, "core.domain", "inventado.presentation")

            val result = ModulePackagesRule.violations(scope, documentedModules = setOf("core"))

            result shouldBeEqualTo listOf("Pacote `inventado` deve constar na tabela de módulos do README")
        }

        @Test
        fun `Deve reportar o módulo quando a tabela o lista sem pacote correspondente`(@TempDir dir: File) {
            val scope = scopeWithPackages(dir, "core.domain")

            val result =
                ModulePackagesRule.violations(scope, documentedModules = setOf("core", "fantasma"))

            result shouldBeEqualTo listOf("Módulo `fantasma` da tabela do README deve ter um pacote")
        }

        @Test
        fun `Deve ignorar os arquivos do pacote raiz quando compara com a tabela`(@TempDir dir: File) {
            val scope = scopeWithPackages(dir, ROOT, "core.domain")

            val result = ModulePackagesRule.violations(scope, documentedModules = setOf("core"))

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o pacote quando ele só começa com o nome do pacote raiz`(@TempDir dir: File) {
            File(dir, "Planted.kt").writeText("package com.fstengineering.leasilycrmfoo\n\nclass Planted\n")
            val scope = Konsist.scopeFromExternalDirectory(dir.absolutePath)

            val result = ModulePackagesRule.violations(scope, documentedModules = emptySet())

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve ignorar o pacote quando ele está fora do pacote raiz`(@TempDir dir: File) {
            File(dir, "Planted.kt").writeText("package org.exemplo.core\n\nclass Planted\n")
            val scope = Konsist.scopeFromExternalDirectory(dir.absolutePath)

            val result = ModulePackagesRule.violations(scope, documentedModules = emptySet())

            result.shouldBeEmpty()
        }

        @Test
        fun `Deve reportar em ordem alfabética quando há mais de uma violação`(@TempDir dir: File) {
            val scope = scopeWithPackages(dir, "zeta", "alfa")

            val result = ModulePackagesRule.violations(scope, documentedModules = emptySet())

            result shouldBeEqualTo listOf(
                "Pacote `alfa` deve constar na tabela de módulos do README",
                "Pacote `zeta` deve constar na tabela de módulos do README",
            )
        }
    }

    private fun scopeWithPackages(dir: File, vararg subpackages: String): KoScope {
        subpackages.forEachIndexed { index, subpackage ->
            val packageName =
                listOf(ROOT_PACKAGE, subpackage).filter { it != ROOT }.joinToString(".")
            File(dir, "Planted$index.kt").writeText("package $packageName\n\nclass Planted$index\n")
        }
        return Konsist.scopeFromExternalDirectory(dir.absolutePath)
    }

    private companion object {
        const val ROOT_PACKAGE = "com.fstengineering.leasilycrm"
        const val ROOT = "<raiz>"
    }
}
