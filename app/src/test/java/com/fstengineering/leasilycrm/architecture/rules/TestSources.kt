package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration

/** Critérios comuns às regras de teste: onde o arquivo mora, o que é teste e o que é código. */
object TestSources {
    private const val ARCHITECTURE_PACKAGE = "com.fstengineering.leasilycrm.architecture"

    private val commentsAndLiterals = Regex(
        """\"\"\"[\s\S]*?\"\"\"|"(?:\\.|[^"\\\n])*"|'(?:\\.|[^'\\])'|/\*[\s\S]*?\*/|//[^\n]*|`[^`\n]*`""",
    )

    private val testAnnotations =
        listOf("Test", "ParameterizedTest", "RepeatedTest", "TestFactory", "TestTemplate")

    fun isUnit(file: KoFileDeclaration) = "/src/test/" in file.path

    fun isInstrumented(file: KoFileDeclaration) = "/src/androidTest/" in file.path

    fun isTest(file: KoFileDeclaration) = isUnit(file) || isInstrumented(file)

    fun isTestFunction(function: KoFunctionDeclaration) =
        testAnnotations.any { function.hasAnnotationWithName(it) }

    fun hasTests(file: KoFileDeclaration) = file.functions().any { isTestFunction(it) }

    fun hasTests(declaration: KoClassDeclaration) =
        declaration.functions().any { isTestFunction(it) }

    /** Testes de arquitetura não têm SUT em `main`: só ficam fora do espelhamento com a produção. */
    fun isArchitecture(file: KoFileDeclaration): Boolean {
        val pkg = file.packagee?.name.orEmpty()
        return pkg == ARCHITECTURE_PACKAGE || pkg.startsWith("$ARCHITECTURE_PACKAGE.")
    }

    /** Só código: comentário, literal de texto e nome entre crases não usam a API, apenas a citam. */
    fun code(file: KoFileDeclaration) = commentsAndLiterals.replace(file.text, " ")
}
