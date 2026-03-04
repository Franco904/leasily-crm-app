package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.persistentListOf

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
        ): AppResultCompound<AssistanceGuestsCount, DomainError.Assistance> {
            val errors = persistentListOf<DomainError.Assistance>()

            if (adultsCount < 0) errors.add(DomainError.Assistance.AdultGuestsCountIsNegative)
            if (kidsCount < 0) errors.add(DomainError.Assistance.KidsGuestsCountIsNegative)
            if (babiesCount < 0) errors.add(DomainError.Assistance.BabiesGuestsCountIsNegative)
            if (animalsCount < 0) errors.add(DomainError.Assistance.AnimalsGuestsCountIsNegative)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors)

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
