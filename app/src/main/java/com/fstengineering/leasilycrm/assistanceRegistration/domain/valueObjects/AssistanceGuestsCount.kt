package com.fstengineering.leasilycrm.assistanceRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
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
        ): AppResult<AssistanceGuestsCount, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (adultsCount < 0) errors.add(DomainError.AssistanceOtherFields.AdultGuestsCountIsNegative)
            if (kidsCount < 0) errors.add(DomainError.AssistanceOtherFields.KidsGuestsCountIsNegative)
            if (babiesCount < 0) errors.add(DomainError.AssistanceOtherFields.BabiesGuestsCountIsNegative)
            if (animalsCount < 0) errors.add(DomainError.AssistanceOtherFields.AnimalsGuestsCountIsNegative)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = AssistanceGuestsCount(
                adultsCount = adultsCount,
                kidsCount = kidsCount,
                babiesCount = babiesCount,
                animalsCount = animalsCount,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
