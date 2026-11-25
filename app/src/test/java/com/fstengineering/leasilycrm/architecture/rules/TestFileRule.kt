package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object TestFileRule {
    private const val ARCHITECTURE_PACKAGE = "com.fstengineering.leasilycrm.architecture"

    fun violations(scope: KoScope): List<String> {
        val productionFiles = scope.files
            .filter { "/src/main/" in it.path }
            .map { it.packagee?.name.orEmpty() to it.name }
            .toSet()

        return scope.files
            .filter { isUnitOrInstrumented(it) && hasTests(it) && !isArchitecture(it) }
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

            pkg to subject !in productionFiles ->
                listOf("$name: não existe `$subject.kt` em main no pacote `$pkg`")

            else -> emptyList()
        }
    }

    private fun isUnitOrInstrumented(file: KoFileDeclaration) =
        "/src/test/" in file.path || "/src/androidTest/" in file.path

    private fun hasTests(file: KoFileDeclaration) =
        file.functions().any { it.hasAnnotationWithName("Test") }

    private fun isArchitecture(file: KoFileDeclaration): Boolean {
        val pkg = file.packagee?.name.orEmpty()
        return pkg == ARCHITECTURE_PACKAGE || pkg.startsWith("$ARCHITECTURE_PACKAGE.")
    }
}
