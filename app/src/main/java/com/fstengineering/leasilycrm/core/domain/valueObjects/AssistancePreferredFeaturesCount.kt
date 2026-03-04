package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf

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
        ): AppResultCompound<AssistancePreferredFeaturesCount, DomainError.Assistance> {
            val errors = persistentListOf<DomainError.Assistance>()

            if (bedroomsCount < 0) errors.add(DomainError.Assistance.BedroomsCountIsNegative)
            if (enSuitesCount < 0) errors.add(DomainError.Assistance.EnSuitesCountIsNegative)
            if (doubleBedsCount < 0) errors.add(DomainError.Assistance.DoubleBedsCountIsNegative)
            if (singleBedsCount < 0) errors.add(DomainError.Assistance.SingleBedsCountIsNegative)
            if (bathroomsCount < 0) errors.add(DomainError.Assistance.BathroomsCountIsNegative)
            if (parkingSpotsCount < 0) errors.add(DomainError.Assistance.ParkingSpotsCountIsNegative)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

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
