package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope

object ModulePackagesRule {
    private const val ROOT_PACKAGE = "com.fstengineering.leasilycrm"

    /**
     * Mensagem estável por violação: pacotes sem módulo no README e depois módulos sem pacote, cada
     * grupo em ordem alfabética; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope, documentedModules: Set<String>): List<String> {
        val actualModules = scope.files
            .mapNotNull { it.packagee?.name }
            .filter { it.startsWith("$ROOT_PACKAGE.") }
            .map { it.removePrefix("$ROOT_PACKAGE.").substringBefore(".") }
            .toSet()

        val undocumented = (actualModules - documentedModules).sorted()
            .map { "Pacote `$it` não consta na tabela de módulos do README" }
        val withoutPackage = (documentedModules - actualModules).sorted()
            .map { "Módulo `$it` da tabela do README não tem pacote" }

        return undocumented + withoutPackage
    }
}
