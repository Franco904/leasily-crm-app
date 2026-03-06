package com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.toPersistentList

data class AssistancePreferredFeaturesCount(
    val bedroomsCount: Int,
    val enSuitesCount: Int,
    val doubleBedsCount: Int,
    val singleBedsCount: Int,
    val bathroomsCount: Int,
    val parkingSpotsCount: Int,
) {
    companion object {
        fun parse(
            bedroomsCount: Int,
            enSuitesCount: Int,
            doubleBedsCount: Int,
            singleBedsCount: Int,
            bathroomsCount: Int,
            parkingSpotsCount: Int,
        ): AppResultCompound<AssistancePreferredFeaturesCount, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (bedroomsCount < 0) errors.add(DomainError.AssistanceOtherFields.BedroomsCountIsNegative)
            if (enSuitesCount < 0) errors.add(DomainError.AssistanceOtherFields.EnSuitesCountIsNegative)
            if (doubleBedsCount < 0) errors.add(DomainError.AssistanceOtherFields.DoubleBedsCountIsNegative)
            if (singleBedsCount < 0) errors.add(DomainError.AssistanceOtherFields.SingleBedsCountIsNegative)
            if (bathroomsCount < 0) errors.add(DomainError.AssistanceOtherFields.BathroomsCountIsNegative)
            if (parkingSpotsCount < 0) errors.add(DomainError.AssistanceOtherFields.ParkingSpotsCountIsNegative)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = AssistancePreferredFeaturesCount(
                bedroomsCount = bedroomsCount,
                enSuitesCount = enSuitesCount,
                doubleBedsCount = doubleBedsCount,
                singleBedsCount = singleBedsCount,
                bathroomsCount = bathroomsCount,
                parkingSpotsCount = parkingSpotsCount,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
