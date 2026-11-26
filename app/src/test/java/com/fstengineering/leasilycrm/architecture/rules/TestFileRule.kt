package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object TestFileRule {
    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> {
        val productionFiles = scope.files
            .filter { "/src/main/" in it.path }
            .map { it.packagee?.name.orEmpty() to it.name }
            .toSet()

        return scope.files
            .filter { TestSources.isTest(it) && TestSources.hasTests(it) }
            .flatMap { violationsOf(it, productionFiles) }
            .sorted()
    }

    private fun violationsOf(
        file: KoFileDeclaration,
        productionFiles: Set<Pair<String, String>>,
    ): List<String> {
        val name = file.nameWithExtension
        val pkg = file.packagee?.name.orEmpty()
        val subject = file.name.removeSuffix("Test")
        return when {
            !file.name.endsWith("Test") ->
                listOf("$name: o nome do arquivo de teste deve terminar em `Test`")

            file.classes(includeNested = false).none { it.name == file.name } ->
                listOf("$name: não declara a classe `${file.name}`")

            file.classes(includeNested = false).count { TestSources.hasTests(it) } > 1 ->
                listOf("$name: declara mais de uma classe de teste")

            pkg to subject !in productionFiles && !TestSources.isArchitecture(file) ->
                listOf("$name: não existe `$subject.kt` em main no pacote `$pkg`")

            else -> emptyList()
        }
    }
}
