package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object UnitTestImportsRule {
    private const val ROOT = "com.fstengineering.leasilycrm"

    private val domainBannedPrefixes = listOf("android.", "androidx.", "io.mockk.")
    private val domainBannedLayers = setOf("infra", "application")
    private val otherJUnitAndAssertions = listOf(
        "org.junit.jupiter.api.Assertions",
        "kotlin.test.",
        "org.hamcrest.",
        "org.assertj.",
        "com.google.common.truth.",
    )
    private val jupiterAssertFunction = Regex("""org\.junit\.jupiter\.api\.assert\w*""")

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> = scope.files
        .filter { TestSources.isUnit(it) }
        .flatMap { file -> file.imports.map { it.name }.mapNotNull { reasonOf(file, it) } }
        .sorted()

    private fun reasonOf(file: KoFileDeclaration, import: String): String? {
        val name = file.nameWithExtension
        return when {
            isDomainTest(file) && isDomainBanned(import) ->
                "$name: teste de domínio não deve importar `$import` " +
                    "(android, androidx, mockk, infra, application)"

            import.startsWith("androidx.room") || import.substringAfterLast('.') == "AppDatabase" ->
                "$name: teste de unidade não deve importar `$import` " +
                    "(banco só em androidTest)"

            isAssertionOutsideKluent(import) ->
                "$name: teste de unidade não deve importar `$import` " +
                    "(só JUnit 5 e kluent)"

            else -> null
        }
    }

    private fun isDomainTest(file: KoFileDeclaration) =
        "domain" in file.packagee?.name.orEmpty().split('.')

    private fun isDomainBanned(import: String): Boolean {
        if (domainBannedPrefixes.any { import.startsWith(it) }) return true
        if (!import.startsWith("$ROOT.")) return false
        return import.removePrefix("$ROOT.").split('.').any { it in domainBannedLayers }
    }

    private fun isAssertionOutsideKluent(import: String): Boolean = when {
        otherJUnitAndAssertions.any { import.startsWith(it) } -> true
        jupiterAssertFunction.matches(import) -> true
        import.startsWith("org.junit.") && !import.startsWith("org.junit.jupiter.") -> true
        else -> false
    }
}
