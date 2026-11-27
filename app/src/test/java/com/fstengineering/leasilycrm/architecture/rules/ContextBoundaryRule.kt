package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * 0003 §2, fronteira de contexto: o módulo só importa o próprio contexto, o `core` e, do pacote
 * raiz, `Destinations` (`R` e `BuildConfig` são gerados). A raiz de composição fica de fora: o que
 * ela pode importar é da [CompositionRootRule].
 */
object ContextBoundaryRule {
    private const val ROOT = "com.fstengineering.leasilycrm"
    private const val CORE = "core"
    private const val TEST_INFRA = "testUtils"
    private val rootPackageImports = setOf("Destinations", "R", "BuildConfig")

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra
     * respeitada. [contexts] é módulo → contexto da tabela do README; módulo ausente ou sem
     * contexto (`null`) é contexto próprio.
     */
    fun violations(scope: KoScope, contexts: Map<String, String?>): List<String> = scope.files
        .filter { !CompositionRootRule.isMember(it) && !isTestInfra(it) }
        .flatMap { file ->
            val module = moduleOf(file) ?: return@flatMap emptyList()
            file.imports.map { it.name }
                .filter { !isAllowed(file, module, it, contexts) }
                .map {
                    "${file.nameWithExtension}: `$module` não deve importar `$it` " +
                        "(só o próprio contexto, o `core` e, do pacote raiz, `Destinations`)"
                }
        }
        .sorted()

    private fun isAllowed(
        file: KoFileDeclaration,
        module: String,
        import: String,
        contexts: Map<String, String?>,
    ): Boolean {
        if (!import.startsWith("$ROOT.")) return true
        val target = import.removePrefix("$ROOT.").substringBefore('.')
        return when {
            target in rootPackageImports -> true
            target == TEST_INFRA && TestSources.isTest(file) -> true
            target == module || target == CORE -> true
            else -> contextOf(target, contexts) == contextOf(module, contexts)
        }
    }

    private fun contextOf(module: String, contexts: Map<String, String?>) =
        contexts[module]?.let { "contexto $it" } ?: "módulo $module"

    private fun isTestInfra(file: KoFileDeclaration) =
        TestSources.isTest(file) && moduleOf(file) == TEST_INFRA

    /** Primeiro pacote abaixo da raiz; `null` fora dela ou no próprio pacote raiz. */
    private fun moduleOf(file: KoFileDeclaration): String? {
        val pkg = file.packagee?.name ?: return null
        if (!pkg.startsWith("$ROOT.")) return null
        return pkg.removePrefix("$ROOT.").substringBefore('.')
    }
}
