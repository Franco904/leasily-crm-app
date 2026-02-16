package com.fstengineering.leasilycrm.core.domain.helpers

import java.math.BigDecimal

data class AppDecimal(val decimal: BigDecimal) {
    val doubleValue = decimal.toDouble()
    val isNegative = decimal.signum() == -1

    override fun toString() = decimal.toString()
}

fun String.toDecimalOrNull(): AppDecimal? {
    val decimal = toBigDecimalOrNull() ?: return null
    return AppDecimal(decimal)
}

fun Int.toDecimal(): AppDecimal {
    return AppDecimal(toBigDecimal())
}
