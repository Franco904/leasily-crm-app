package com.fstengineering.leasilycrm.core.presentation.utils

import java.util.Locale

fun String.capitalizeFirst() = this.lowercase()
    .replaceFirstChar { it.titlecase(Locale.getDefault()) }

fun String?.orNotRegistered(notRegisteredString: String): String =
    this?.takeUnless { it.isBlank() } ?: notRegisteredString
