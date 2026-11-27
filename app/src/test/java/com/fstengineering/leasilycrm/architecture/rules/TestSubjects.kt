package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoBaseDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.provider.KoContainingDeclarationProvider
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider

/** O SUT de um teste de unidade: o arquivo de produção de mesmo pacote e nome (sem `Test`). */
object TestSubjects {
    fun productionFiles(scope: KoScope): Map<Pair<String, String>, KoFileDeclaration> = scope.files
        .filter { "/src/main/" in it.path }
        .associateBy { it.packagee?.name.orEmpty() to it.name }

    fun subjectOf(
        test: KoFileDeclaration,
        productionFiles: Map<Pair<String, String>, KoFileDeclaration>,
    ): KoFileDeclaration? =
        productionFiles[test.packagee?.name.orEmpty() to test.name.removeSuffix("Test")]

    /**
     * Nomes dos métodos públicos do SUT, em qualquer profundidade do arquivo (top-level, classe,
     * companion, classe aninhada) cuja cadeia de classes também é pública; sobrecargas e mesmo
     * nome em classes distintas contam uma vez. Override `protected` conta, declarado ou herdado:
     * o Konsist não resolve a visibilidade herdada, e escrevê-la não pode mudar o resultado.
     */
    fun publicMethods(subject: KoFileDeclaration): Set<String> = subject
        .functions(includeNested = true, includeLocal = false)
        .filter {
            it.hasPublicOrDefaultModifier || (it.hasProtectedModifier && it.hasOverrideModifier)
        }
        .filter { isReachable(it.containingDeclaration) }
        .map { it.name }
        .toSet()

    /** Funções públicas e propriedades públicas do SUT: o que um grupo de teste pode nomear. */
    fun publicMembers(subject: KoFileDeclaration): Set<String> = publicMethods(subject) + subject
        .properties(includeNested = true)
        .filter { it.hasPublicOrDefaultModifier && isReachable(it.containingDeclaration) }
        .map { it.name }
        .toSet()

    private fun isReachable(container: KoBaseDeclaration): Boolean {
        if (container is KoFileDeclaration) return true
        if (container is KoVisibilityModifierProvider && !container.hasPublicOrDefaultModifier) {
            return false
        }
        return (container as? KoContainingDeclarationProvider)
            ?.let { isReachable(it.containingDeclaration) } ?: true
    }
}
