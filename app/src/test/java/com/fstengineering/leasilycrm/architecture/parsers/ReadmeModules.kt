package com.fstengineering.leasilycrm.architecture.parsers

object ReadmeModules {
    private const val SECTION_HEADING = "## Estrutura do Projeto"
    private const val NO_CONTEXT = "—"
    private val moduleRow = Regex("""^\|\s*`([A-Za-z][A-Za-z0-9]*)`\s*\|""")

    /** Módulo → contexto delimitado; `null` quando o módulo não tem contexto (`—`). */
    fun parse(readme: String): Map<String, String?> {
        val lines = readme.lines()
        val start = lines.indexOfFirst { it.trim() == SECTION_HEADING }
        check(start >= 0) { "README deve ter a seção '$SECTION_HEADING'" }

        return lines
            .drop(start + 1)
            .takeWhile { !it.startsWith("## ") }
            .mapNotNull { line ->
                moduleRow.find(line)?.let { it.groupValues[1] to contextOf(line) }
            }
            .toMap()
    }

    private fun contextOf(row: String): String? {
        val context = row.split('|').getOrNull(2)?.trim()
        check(!context.isNullOrEmpty()) { "Linha de módulo deve ter a coluna de contexto: $row" }
        return context.takeUnless { it.startsWith(NO_CONTEXT) }
    }
}
