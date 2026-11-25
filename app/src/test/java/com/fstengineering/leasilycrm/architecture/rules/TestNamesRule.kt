package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration

object TestNamesRule {
    private val unitTemplate = Regex("Deve .+ quando .+")
    private val instrumentedTemplate = Regex("deve[A-Z]\\w*Quando[A-Z]\\w*")
    private val codeInDescription = Regex("[._=()]|[a-zà-ú][A-Z]")

    fun violations(scope: KoScope): List<String> = scope.functions()
        .filter { it.hasAnnotationWithName("Test") }
        .flatMap { violationsOf(it) }
        .sorted()

    private fun violationsOf(function: KoFunctionDeclaration): List<String> {
        val path = function.containingFile.path
        val file = function.containingFile.nameWithExtension
        val name = function.name
        return when {
            "/src/test/" in path -> unitViolations(file, name)
            "/src/androidTest/" in path -> instrumentedViolations(file, name)
            else -> emptyList()
        }
    }

    private fun unitViolations(file: String, name: String): List<String> = when {
        !unitTemplate.matches(name) -> listOf(
            "$file: `$name` foge do template `Deve [resultado] quando [ação]` de test/",
        )

        codeInDescription.containsMatchIn(name) -> listOf(
            "$file: `$name` tem código na descrição",
        )

        else -> emptyList()
    }

    private fun instrumentedViolations(file: String, name: String): List<String> =
        if (instrumentedTemplate.matches(name)) {
            emptyList()
        } else {
            listOf("$file: `$name` foge do template `deve<Resultado>Quando<Condição>` de androidTest/")
        }
}
