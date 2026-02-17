package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.DomainError

enum class PropertyType(val id: Long) {
    Apartment(id = 1L),
    House(id = 2L);

    companion object {
        fun parse(id: Long): PropertyType = entries.find { it.id == id }
            ?: throw DomainError.Property.TypeInvalid.asException()
    }
}
