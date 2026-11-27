package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object MissingTestGroupsRule {
    /**
     * Todo método de [TestSubjects.publicMethods] deve ter um grupo `@Nested` com teste no arquivo
     * de teste de unidade do SUT. Propriedade não é exigida, e SUT sem arquivo de teste com ao
     * menos um `@Test` não é cobrado.
     *
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra
     * respeitada.
     */
    fun violations(scope: KoScope): List<String> {
        val productionFiles = TestSubjects.productionFiles(scope)

        return scope.files
            .filter { TestSources.isUnit(it) && TestSources.hasTests(it) }
            .filterNot { TestSources.isArchitecture(it) }
            .flatMap { violationsOf(it, productionFiles) }
            .sorted()
    }

    private fun violationsOf(
        file: KoFileDeclaration,
        productionFiles: Map<Pair<String, String>, KoFileDeclaration>,
    ): List<String> {
        val testClass = file.classes(includeNested = false).firstOrNull { it.name == file.name }
        val subject = TestSubjects.subjectOf(file, productionFiles)
        if (testClass == null || subject == null) return emptyList()

        val grouped = testClass.classes(includeNested = false)
            .filter { it.hasAnnotationWithName("Nested") && TestSources.hasTests(it) }
            .mapNotNull { TestGroupsRule.displayName(it) }
            .toSet()

        return (TestSubjects.publicMethods(subject) - grouped).map {
            "${file.nameWithExtension}: o método público `$it` do SUT deve ter um grupo " +
                "`@Nested` `${TestGroupsRule.groupName(it)}` com teste"
        }
    }
}
