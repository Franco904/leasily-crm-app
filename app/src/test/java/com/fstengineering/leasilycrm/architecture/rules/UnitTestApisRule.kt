package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

object UnitTestApisRule {
    private val blockingCall = Regex("""\brunBlocking\b""")
    private val relaxedMock = Regex("""\brelaxed\s*=""")
    private val mockkCall = Regex("""\bmockk\b""")
    private const val RELAX_UNIT_FUN = "relaxUnitFun = true"

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> = scope.files
        .filter { TestSources.isUnit(it) }
        .flatMap { violationsOf(it) }
        .sorted()

    private fun violationsOf(file: KoFileDeclaration): List<String> {
        val name = file.nameWithExtension
        val code = TestSources.code(file).lines()
            .filterNot { it.trimStart().startsWith("import ") }
            .joinToString("\n")

        val blocking = if (blockingCall.containsMatchIn(code)) {
            listOf("$name: teste de unidade não deve usar `runBlocking`; use `runTest`")
        } else {
            emptyList()
        }
        val relaxedMocks = if (relaxedMock.containsMatchIn(code)) {
            listOf("$name: mock não deve usar `relaxed = true`; use `mockk(relaxUnitFun = true)`")
        } else {
            emptyList()
        }
        val strictMocks = if (mockkCalls(code).any { RELAX_UNIT_FUN !in it }) {
            listOf("$name: toda criação de mock deve levar `relaxUnitFun = true`")
        } else {
            emptyList()
        }
        return blocking + relaxedMocks + strictMocks
    }

    /** Argumentos (entre parênteses) de cada `mockk`; chamada sem parênteses vira texto vazio. */
    private fun mockkCalls(code: String): List<String> = mockkCall.findAll(code).map { match ->
        var i = skipSpaces(code, match.range.last + 1)
        if (i < code.length && code[i] == '<') i = skipSpaces(code, closing(code, i, '<', '>') + 1)
        if (i < code.length && code[i] == '(') {
            code.substring(i, closing(code, i, '(', ')') + 1)
        } else {
            ""
        }
    }.toList()

    private fun skipSpaces(text: String, from: Int): Int {
        var i = from
        while (i < text.length && text[i].isWhitespace()) i++
        return i
    }

    private fun closing(text: String, open: Int, opener: Char, closer: Char): Int {
        var depth = 0
        for (i in open until text.length) {
            when (text[i]) {
                opener -> depth++
                closer -> if (--depth == 0) return i
            }
        }
        return text.lastIndex
    }
}
