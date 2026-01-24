package com.fstengineering.leasilycrm.core.domain.models.wrappers

import java.math.BigDecimal

data class AppDecimal(val decimal: BigDecimal) {
    fun toDouble() = decimal.toDouble()

    override fun toString() = decimal.toString()
}

fun String.toDecimalOrNull(): AppDecimal? {
    val decimal = toBigDecimalOrNull() ?: return null
    return AppDecimal(decimal)
}

fun Int.toDecimal(): AppDecimal {
    return AppDecimal(toBigDecimal())
}
