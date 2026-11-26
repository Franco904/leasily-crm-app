package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope

object TestDaoRule {
    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> {
        val productionFiles = TestSubjects.productionFiles(scope)

        return scope.files
            .filter { TestSources.isUnit(it) && TestSources.hasTests(it) }
            .mapNotNull { test ->
                val subject =
                    TestSubjects.subjectOf(test, productionFiles) ?: return@mapNotNull null
                val isDao =
                    subject.classes(includeNested = true).any { it.hasAnnotationWithName("Dao") } ||
                        subject.interfaces(includeNested = true)
                            .any { it.hasAnnotationWithName("Dao") }
                if (isDao) {
                    "${test.nameWithExtension}: `${subject.name}` é @Dao e deve ser testado só em androidTest"
                } else {
                    null
                }
            }
            .sorted()
    }
}
