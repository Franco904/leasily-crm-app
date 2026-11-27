package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object TestGroupsRule {
    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
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

        val members = TestSubjects.publicMembers(subject)
        val name = file.nameWithExtension

        val looseTests = testClass.functions(includeNested = false)
            .filter { TestSources.isTestFunction(it) }
            .map {
                "$name: `${it.name}` deve estar numa classe `@Nested` de método público do SUT"
            }

        val groups = testClass.classes(includeNested = false)
        val groupViolations = groups.flatMap { groupViolations(name, it, members) }

        val duplicated = groups
            .filter { it.hasAnnotationWithName("Nested") }
            .mapNotNull { displayName(it) }
            .groupingBy { it }.eachCount()
            .filterValues { it > 1 }.keys
            .map { "$name: o método `$it` deve ter um único grupo `@Nested`" }

        return looseTests + groupViolations + duplicated
    }

    private fun groupViolations(
        file: String,
        group: KoClassDeclaration,
        members: Set<String>,
    ): List<String> {
        val method = displayName(group)
        return when {
            group.classes(includeNested = true).any { TestSources.hasTests(it) } ->
                listOf("$file: `${group.name}` não deve aninhar outra classe de teste")

            !group.hasAnnotationWithName("Nested") ->
                if (TestSources.hasTests(group)) {
                    listOf("$file: `${group.name}` tem testes e deve ser `@Nested`")
                } else {
                    emptyList()
                }

            method == null ->
                listOf("$file: `${group.name}` deve ter `@DisplayName` com o nome do método")

            method !in members ->
                listOf(
                    "$file: `@DisplayName(\"$method\")` deve nomear um método ou propriedade pública do SUT",
                )

            group.name != groupName(method) ->
                listOf("$file: `${group.name}` deve se chamar `${groupName(method)}`")

            else -> emptyList()
        }
    }

    fun groupName(method: String) = method.replaceFirstChar { it.uppercase() } + "Tests"

    fun displayName(group: KoClassDeclaration): String? = group.annotations
        .firstOrNull { it.name == "DisplayName" }
        ?.arguments?.firstOrNull()?.value?.toString()
}
