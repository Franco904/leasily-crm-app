package com.fstengineering.leasilycrm.architecture

import com.fstengineering.leasilycrm.architecture.parsers.ReadmeModules
import com.fstengineering.leasilycrm.architecture.rules.ModulePackagesRule
import com.lemonappdev.konsist.api.Konsist
import org.amshove.kluent.shouldBeEmpty
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

        result.shouldBeEmpty()
    }
}
