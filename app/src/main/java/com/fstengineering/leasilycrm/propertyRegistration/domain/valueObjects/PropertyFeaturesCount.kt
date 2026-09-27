package com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import kotlinx.collections.immutable.toPersistentList

data class PropertyFeaturesCount(
    val bedroomsCount: Int,
    val enSuitesCount: Int,
    val doubleBedsCount: Int,
    val singleBedsCount: Int,
    val bathroomsCount: Int,
    val parkingSpotsCount: Int,
    val airConditionersCount: Int,
    val maxGuestsCount: Int,
) {
    companion object {
        fun parse(
            bedroomsCount: Int,
            enSuitesCount: Int,
            doubleBedsCount: Int,
            singleBedsCount: Int,
            bathroomsCount: Int,
            parkingSpotsCount: Int,
            airConditionersCount: Int,
            maxGuestsCount: Int,
        ): AppResult<PropertyFeaturesCount, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (bedroomsCount < 0) errors.add(DomainError.PropertyOtherFields.BedroomsCountIsNegative)
            if (enSuitesCount < 0) errors.add(DomainError.PropertyOtherFields.EnSuitesCountIsNegative)
            if (doubleBedsCount < 0) errors.add(DomainError.PropertyOtherFields.DoubleBedsCountIsNegative)
            if (singleBedsCount < 0) errors.add(DomainError.PropertyOtherFields.SingleBedsCountIsNegative)
            if (bathroomsCount < 0) errors.add(DomainError.PropertyOtherFields.BathroomsCountIsNegative)
            if (parkingSpotsCount < 0) errors.add(DomainError.PropertyOtherFields.ParkingSpotsCountIsNegative)
            if (airConditionersCount < 0) errors.add(DomainError.PropertyOtherFields.AirConditionersCountIsNegative)
            if (maxGuestsCount < 0) errors.add(DomainError.PropertyOtherFields.MaxGuestsCountIsNegative)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = PropertyFeaturesCount(
                bedroomsCount = bedroomsCount,
                enSuitesCount = enSuitesCount,
                doubleBedsCount = doubleBedsCount,
                singleBedsCount = singleBedsCount,
                bathroomsCount = bathroomsCount,
                parkingSpotsCount = parkingSpotsCount,
                airConditionersCount = airConditionersCount,
                maxGuestsCount = maxGuestsCount,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
