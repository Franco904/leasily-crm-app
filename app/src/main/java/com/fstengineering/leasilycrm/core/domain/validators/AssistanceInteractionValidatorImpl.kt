package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceInteractionValidator

class AssistanceInteractionValidatorImpl : AssistanceInteractionValidator {
    override fun validate(
        interaction: AssistanceInteraction,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateInteractionDate(interactionDate = interaction.interactionDate)
            .onError { e -> errors.add(e) }

        validateTitle(title = interaction.title)
            .onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateInteractionDate(interactionDate: AppDate?): AppResult<Unit, DomainError.AssistanceInteractionDate> {
        return when {
            interactionDate == null -> AppResult.Error(DomainError.AssistanceInteractionDate.IsNull)
            else -> AppResult.Success(Unit)
        }
    }

    private fun validateTitle(title: String?): AppResult<Unit, DomainError.AssistanceInteractionTitle> {
        return when {
            title.isNullOrBlank() -> {
                AppResult.Error(DomainError.AssistanceInteractionTitle.IsNullOrBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
