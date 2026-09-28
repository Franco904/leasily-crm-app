package com.fstengineering.leasilycrm.core.domain.valueObjects

class Amount private constructor(val value: AppDecimal) {
    companion object {
        fun of(value: AppDecimal): Amount = Amount(AppDecimal(value.decimal.abs()))
    }
}
