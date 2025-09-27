package com.fstengineering.leasilycrm.core.presentation.utils

fun Int.formatToTwoDigitsDateTime(): String {
    return if (toString().length == 1) "0$this" else this.toString()
}
