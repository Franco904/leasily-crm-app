package com.fstengineering.leasilycrm.core.presentation.utils

import java.text.Normalizer
import java.util.Locale

fun String.capitalizeFirst() = this
    .lowercase()
    .replaceFirstChar { it.titlecase(Locale.getDefault()) }

fun String.normalizeEncoding(): String =
    Normalizer.normalize(this, Normalizer.Form.NFC)?.trim() ?: this.trim()

fun String?.orNotRegistered(notRegisteredString: String): String =
    this?.takeUnless { it.isBlank() } ?: notRegisteredString
