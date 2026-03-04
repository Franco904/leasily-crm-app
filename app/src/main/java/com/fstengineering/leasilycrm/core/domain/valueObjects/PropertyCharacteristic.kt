package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

enum class PropertyCharacteristic(val id: Long) {
    Accessibility(id = 1L),
    Balcony(id = 2L),
    BarbecueArea(id = 3L),
    BathLinen(id = 4L),
    BeachChair(id = 5L),
    BeachUmbrella(id = 6L),
    BedLinen(id = 7L),
    CommercialArea(id = 8L),
    CommonAreaCondominium(id = 9L),
    Elevator(id = 10L),
    HomeOffice(id = 11L),
    Modern(id = 12L),
    PetFriendly(id = 13L),
    Playground(id = 14L),
    SwimmingPool(id = 15L),
    WashingMachine(id = 16L);

    companion object {
        fun parse(id: Long): AppResult<PropertyCharacteristic, DomainError.Property> {
            val characteristic = entries.find { it.id == id }

            return characteristic?.let { AppResult.Success(data = it) }
                ?: AppResult.Error(DomainError.Property.CharacteristicInvalid)
        }
    }
}
