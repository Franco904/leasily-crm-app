package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceInteractionValidator
import java.time.LocalDate

class AssistanceInteractionValidatorImpl : AssistanceInteractionValidator {
    override fun validate(
        interaction: AssistanceInteraction,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateInteractionDate(interactionDate = interaction.interactionDate)
            .onError { e -> errors.add(e) }

        validateTitle(title = interaction.title)
            .onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateInteractionDate(interactionDate: LocalDate?): AppResult<Unit, ValidationError.AssistanceInteractionDate> {
        return when {
            interactionDate == null -> AppResult.Error(ValidationError.AssistanceInteractionDate.IsNull)
            else -> AppResult.Success(Unit)
        }
    }

    private fun validateTitle(title: String?): AppResult<Unit, ValidationError.AssistanceInteractionTitle> {
        return when {
            title.isNullOrBlank() -> {
                AppResult.Error(ValidationError.AssistanceInteractionTitle.IsNullOrBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
