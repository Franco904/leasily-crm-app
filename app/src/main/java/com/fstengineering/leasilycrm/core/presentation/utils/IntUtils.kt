package com.fstengineering.leasilycrm.core.presentation.utils

fun Int.formatToTwoDigitsDateTime(): String = toString().padStart(2, '0')
