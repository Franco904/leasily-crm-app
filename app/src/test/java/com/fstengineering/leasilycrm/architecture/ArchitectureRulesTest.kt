package com.fstengineering.leasilycrm.architecture

import com.fstengineering.leasilycrm.architecture.parsers.ReadmeModules
import com.fstengineering.leasilycrm.architecture.rules.ModulePackagesRule
import com.fstengineering.leasilycrm.architecture.rules.TestFileRule
import com.fstengineering.leasilycrm.architecture.rules.TestNamesRule
import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.File

class ArchitectureRulesTest {
    @Test
    fun `Deve manter os pacotes de primeiro nível iguais aos módulos do README quando o código de produção evolui`() {
        val readme = File(Konsist.projectRootPath, "README.md").readText()

        val result = ModulePackagesRule.violations(
            scope = Konsist.scopeFromProduction(),
            documentedModules = ReadmeModules.parse(readme),
        )

        result shouldBeEqualTo emptyList()
    }

    @Test
    fun `Deve manter os nomes dos testes no template quando o código de teste evolui`() {
        val result = TestNamesRule.violations(Konsist.scopeFromProject())

        result shouldBeEqualTo emptyList()
    }

    @Test
    fun `Deve espelhar cada arquivo de teste no arquivo de produção quando o código de teste evolui`() {
        val result = TestFileRule.violations(Konsist.scopeFromProject())

        result shouldBeEqualTo emptyList()
    }
}
