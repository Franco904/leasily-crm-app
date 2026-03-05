package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class AssistanceGuestsCount private constructor(
    val adultsCount: Int,
    val kidsCount: Int,
    val babiesCount: Int,
    val animalsCount: Int,
) {
    companion object {
        fun parse(
            adultsCount: Int,
            kidsCount: Int,
            babiesCount: Int,
            animalsCount: Int,
        ): AppResultCompound<AssistanceGuestsCount, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (adultsCount < 0) errors.add(DomainError.AssistanceOtherFields.AdultGuestsCountIsNegative)
            if (kidsCount < 0) errors.add(DomainError.AssistanceOtherFields.KidsGuestsCountIsNegative)
            if (babiesCount < 0) errors.add(DomainError.AssistanceOtherFields.BabiesGuestsCountIsNegative)
            if (animalsCount < 0) errors.add(DomainError.AssistanceOtherFields.AnimalsGuestsCountIsNegative)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = AssistanceGuestsCount(
                adultsCount = adultsCount,
                kidsCount = kidsCount,
                babiesCount = babiesCount,
                animalsCount = animalsCount,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
