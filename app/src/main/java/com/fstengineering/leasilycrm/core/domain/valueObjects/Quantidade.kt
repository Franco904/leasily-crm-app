package com.fstengineering.leasilycrm.core.domain.valueObjects

@JvmInline
value class Quantidade private constructor(val value: Int) {
    companion object {
        fun of(value: Int): Quantidade = Quantidade(value.coerceAtLeast(0))
    }
}
