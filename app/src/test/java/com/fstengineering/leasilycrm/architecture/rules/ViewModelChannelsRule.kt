package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration

object ViewModelChannelsRule {
    /**
     * Todo canal de propriedade de ViewModel (classe de sufixo `ViewModel` ou que herda de
     * `ViewModel`) deve ser fechado no `onCleared` da própria classe. Canal é a propriedade, do
     * corpo ou do construtor, de tipo declarado `Channel` ou criada por `Channel(…)`, direto ou
     * em `by lazy`. `Channel` de fora de um ViewModel (ex.: `remember` no Composable) não tem
     * descarte e fica fora.
     *
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra
     * respeitada.
     */
    fun violations(scope: KoScope): List<String> = scope.files
        .filter { "/src/main/" in it.path }
        .flatMap { file ->
            file.classes(includeNested = true)
                .filter { isViewModel(it) }
                .flatMap { viewModel ->
                    unclosedChannels(viewModel).map {
                        "${file.nameWithExtension}: o canal `$it` de `${viewModel.name}` deve " +
                            "ser fechado em `onCleared` com `$it.close()`"
                    }
                }
        }
        .sorted()

    private fun isViewModel(declaration: KoClassDeclaration) =
        declaration.name.endsWith("ViewModel") || declaration.hasParentWithName("ViewModel")

    private fun unclosedChannels(viewModel: KoClassDeclaration): List<String> {
        val disposal = viewModel.functions(includeNested = false, includeLocal = false)
            .filter { it.name == "onCleared" }
            .joinToString("\n") { TestSources.code(it.text) }

        return viewModel.properties(includeNested = false)
            .filter { declaresChannel(it.name, TestSources.code(it.text)) }
            .map { it.name }
            .filterNot { closes(disposal, it) }
    }

    /** Ancorado no nome: canal local criado dentro do inicializador não é a propriedade. */
    private fun declaresChannel(name: String, declaration: String): Boolean {
        val typed = """:\s*Channel\b"""
        val created = """(?::[^={]*)?(?:=|by\s+lazy\s*(?:\([^)]*\))?\s*\{)""" +
            """\s*Channel\s*(?:<[\s\S]*?>)?\s*\("""

        return Regex("""\b(?:val|var)\s+$name\s*(?:$typed|$created)""")
            .containsMatchIn(declaration)
    }

    private fun closes(disposal: String, name: String) =
        Regex("""(?<![\w.])(?:this\s*\.\s*)?$name\s*\??\.\s*close\s*\(""")
            .containsMatchIn(disposal)
}
