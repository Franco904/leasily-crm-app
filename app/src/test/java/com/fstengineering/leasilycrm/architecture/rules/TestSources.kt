package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration

/** Localização dos arquivos de teste, critério comum às regras da 0002 em diante. */
object TestSources {
    private const val ARCHITECTURE_PACKAGE = "com.fstengineering.leasilycrm.architecture"

    fun isUnit(file: KoFileDeclaration) = "/src/test/" in file.path

    fun isInstrumented(file: KoFileDeclaration) = "/src/androidTest/" in file.path

    fun isTest(file: KoFileDeclaration) = isUnit(file) || isInstrumented(file)

    private val testAnnotations =
        listOf("Test", "ParameterizedTest", "RepeatedTest", "TestFactory", "TestTemplate")

    fun isTestFunction(function: KoFunctionDeclaration) =
        testAnnotations.any { function.hasAnnotationWithName(it) }

    fun hasTests(file: KoFileDeclaration) = file.functions().any { isTestFunction(it) }

    fun hasTests(declaration: KoClassDeclaration) =
        declaration.functions().any { isTestFunction(it) }

    /** Testes de arquitetura não têm SUT em `main`: ficam fora das regras de teste de unidade. */
    fun isArchitecture(file: KoFileDeclaration): Boolean {
        val pkg = file.packagee?.name.orEmpty()
        return pkg == ARCHITECTURE_PACKAGE || pkg.startsWith("$ARCHITECTURE_PACKAGE.")
    }
}
