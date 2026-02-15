package com.fstengineering.leasilycrm.core.domain.models

sealed interface StayProperty {
    val propertyId: Long

    data class WithId(override val propertyId: Long) : StayProperty

    data class WithData(val data: Property) : StayProperty {
        override val propertyId: Long get() = requireNotNull(data.id)
    }
}
