package com.fstengineering.leasilycrm.core.presentation.utils

import java.text.Normalizer
import java.util.Locale

fun String.capitalizeFirst() = this
    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun String.normalizeEncoding(): String =
    Normalizer.normalize(this, Normalizer.Form.NFC)?.trim() ?: this.trim()

fun String.formatPercent() = this
    .replace(".0", "")
    .replace(".", ",")

fun String?.orNotRegistered(notRegisteredString: String): String =
    this?.takeUnless { it.isBlank() } ?: notRegisteredString
