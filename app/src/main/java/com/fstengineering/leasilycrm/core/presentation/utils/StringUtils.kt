package com.fstengineering.leasilycrm.core.presentation.utils

import java.util.Locale

fun String.capitalizeFirst(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

fun String?.orNotRegistered(notRegisteredString: String): String =
    this?.takeUnless { it.isBlank() } ?: notRegisteredString
