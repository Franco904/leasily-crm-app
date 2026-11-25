package com.fstengineering.leasilycrm.architecture.rules

import com.lemonappdev.konsist.api.container.KoScope

object TestApisRule {
    private val bannedApis = listOf(
        Regex("""\b(un)?mockk(Static|Object|Constructor)\b"""),
        Regex("""\b(atLeast|atMost)(?=\s*=)"""),
        Regex("""\b(co)?[vV]erify(Order|Sequence)\b"""),
        Regex("""import (java\.lang\.reflect|kotlin\.reflect\.(full|jvm))\.[\w.*]+"""),
        Regex("""\b(getDeclared\w+|setAccessible)\b"""),
    )

    fun violations(scope: KoScope): List<String> = scope.files
        .filter { TestSources.isTest(it) && !TestSources.isArchitecture(it) }
        .flatMap { file ->
            bannedApis
                .flatMap { api -> api.findAll(file.text).map { it.value } }
                .distinct()
                .map { "${file.nameWithExtension}: `$it` é proibido em testes" }
        }
        .sorted()
}
