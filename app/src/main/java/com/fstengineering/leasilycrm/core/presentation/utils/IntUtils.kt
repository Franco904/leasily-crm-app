package com.fstengineering.leasilycrm.core.presentation.utils

fun Int.formatToTwoDigitsDay(): String {
    return if (toString().length == 1) "0$this" else this.toString()
}
