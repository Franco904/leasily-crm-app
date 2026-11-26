package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope

object TestApisRule {
    private val bannedApis = listOf(
        Regex("""\b(un)?mockk(Static|Object|Constructor)\b"""),
        Regex("""\b(atLeast|atMost)(?=\s*=)"""),
        Regex("""\b(co)?[vV]erify(Order|Sequence)\b"""),
        Regex("""import (java\.lang\.reflect|kotlin\.reflect\.(full|jvm))\.[\w.*]+"""),
        Regex("""@(BeforeAll|AfterAll|BeforeClass|AfterClass|ClassRule|TestInstance)\b"""),
        Regex("""\b(getDeclared\w+|setAccessible)\b"""),
    )

    /**
     * Violações em ordem alfabética, uma mensagem estável por violação; lista vazia = regra respeitada.
     */
    fun violations(scope: KoScope): List<String> = scope.files
        .filter { TestSources.isTest(it) }
        .flatMap { file ->
            bannedApis
                .flatMap { api -> api.findAll(TestSources.code(file)).map { it.value } }
                .distinct()
                .map { "${file.nameWithExtension}: teste não deve usar `$it`" }
        }
        .sorted()
}
