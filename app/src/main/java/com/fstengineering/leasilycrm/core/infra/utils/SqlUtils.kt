package com.fstengineering.leasilycrm.core.infra.utils

/** Escapa `\`, `%` e `_` para o texto valer como literal num `LIKE ... ESCAPE '\'`. */
fun String.escapeLike() =
    replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
