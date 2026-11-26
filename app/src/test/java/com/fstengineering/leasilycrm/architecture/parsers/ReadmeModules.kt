package com.fstengineering.leasilycrm.architecture.parsers

object ReadmeModules {
    private const val SECTION_HEADING = "## Estrutura do Projeto"
    private val moduleRow = Regex("""^\|\s*`([A-Za-z][A-Za-z0-9]*)`\s*\|""")

    fun parse(readme: String): Set<String> {
        val lines = readme.lines()
        val start = lines.indexOfFirst { it.trim() == SECTION_HEADING }
        check(start >= 0) { "README deve ter a seção '$SECTION_HEADING'" }

        return lines
            .drop(start + 1)
            .takeWhile { !it.startsWith("## ") }
            .mapNotNull { moduleRow.find(it)?.groupValues?.get(1) }
            .toSet()
    }
}
