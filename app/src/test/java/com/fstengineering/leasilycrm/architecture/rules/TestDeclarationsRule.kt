package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoBaseDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.provider.KoNameProvider

object TestDeclarationsRule {
    private val lifecycleAnnotations = listOf(
        "BeforeEach", "AfterEach", "Before", "After",
    )
    private val mockCreation = Regex("""=\s*mockk\s*[(<]""")
    private val mockVariable = Regex("""\b(?:val|var)\s+(\w+)\s*(?::[^=\n]+)?=\s*mockk\s*[(<]""")
    private val mockVariableSuffixed = Regex("""\bvar\s+(\w+Mock)\b""")
    private val mockVariableMutable =
        Regex("""\bvar\s+(\w+)\s*(?::[^=\n]+)?=\s*mockk\s*[(<]""")
    private val sutCreation = Regex("""\b(?:val|var)\s+(\w+)\s*(?::[^=\n]+)?=\s*createSut\(""")

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> = scope.files
        .filter { TestSources.isTest(it) && TestSources.hasTests(it) }
        .flatMap { file -> orderViolations(file) + mockNameViolations(file) + mockMutabilityViolations(file) + sutNameViolations(file) }
        .sorted()

    private fun orderViolations(file: KoFileDeclaration): List<String> =
        file.classes(includeNested = true).flatMap { cls ->
            var previous: Pair<String, Category>? = null
            cls.declarations(includeNested = false, includeLocal = false)
                .mapNotNull { declaration ->
                    val category = categoryOf(declaration) ?: return@mapNotNull null
                    val name = (declaration as KoNameProvider).name
                    val last = previous
                    if (last == null || category.ordinal >= last.second.ordinal) {
                        previous = name to category
                        null
                    } else {
                        "${file.nameWithExtension}: em `${cls.name}`, `$name` (${category.label}) " +
                            "deve vir antes de `${last.first}` (${last.second.label})"
                    }
                }
        }

    private fun mockNameViolations(file: KoFileDeclaration): List<String> =
        mockVariable.findAll(TestSources.code(file))
            .map { it.groupValues[1] }
            .filterNot { it.endsWith("Mock") }
            .distinct()
            .map { "${file.nameWithExtension}: `$it` é um mock e deve terminar em `Mock`" }
            .toList()

    private fun mockMutabilityViolations(file: KoFileDeclaration): List<String> =
        TestSources.code(file).let { code ->
            mockVariableSuffixed.findAll(code) + mockVariableMutable.findAll(code)
        }
            .map { it.groupValues[1] }
            .distinct()
            .map { "${file.nameWithExtension}: `$it` é um mock e deve ser `val` inicializado na declaração" }
            .toList()

    private fun sutNameViolations(file: KoFileDeclaration): List<String> =
        sutCreation.findAll(TestSources.code(file))
            .map { it.groupValues[1] }
            .filterNot { it == "sut" }
            .distinct()
            .map { "${file.nameWithExtension}: `$it` é a instância do SUT e deve se chamar `sut`" }
            .toList()

    private fun categoryOf(declaration: KoBaseDeclaration): Category? = when (declaration) {
        is KoPropertyDeclaration -> when {
            declaration.name.endsWith("Mock") || mockCreation.containsMatchIn(declaration.text) -> Category.MOCKS
            declaration.name == "sut" -> Category.SUT
            else -> Category.CONSTANTS
        }

        is KoFunctionDeclaration -> when {
            TestSources.isTestFunction(declaration) -> Category.TESTS
            lifecycleAnnotations.any { declaration.hasAnnotationWithName(it) } -> Category.LIFECYCLE
            else -> Category.HELPERS
        }

        is KoClassDeclaration ->
            if (declaration.hasAnnotationWithName("Nested")) Category.TESTS else Category.HELPERS

        is KoNameProvider -> Category.HELPERS
        else -> null
    }

    private enum class Category(val label: String) {
        MOCKS("mocks"),
        SUT("sut"),
        CONSTANTS("constantes"),
        LIFECYCLE("setUp/tearDown"),
        TESTS("testes"),
        HELPERS("auxiliares"),
    }
}
