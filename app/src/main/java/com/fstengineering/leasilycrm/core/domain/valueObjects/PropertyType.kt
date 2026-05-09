package com.fstengineering.leasilycrm.core.domain.valueObjects

enum class PropertyType(val id: Long) {
    Apartment(id = 1L),
    House(id = 2L);

    companion object {
        fun fromId(id: Long) = entries.find { it.id == id }
    }
}
