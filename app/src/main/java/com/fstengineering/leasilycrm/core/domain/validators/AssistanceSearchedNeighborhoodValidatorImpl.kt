package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceSearchedNeighborhoodValidator

class AssistanceSearchedNeighborhoodValidatorImpl : AssistanceSearchedNeighborhoodValidator {
    override fun validate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateNeighborhoodName(neighborhoodName = searchedNeighborhood.neighborhood)
            .onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateNeighborhoodName(
        neighborhoodName: String?,
    ): AppResult<Unit, ValidationError.AssistanceSearchedNeighborhoodName> {
        return when {
            neighborhoodName.isNullOrBlank() -> {
                AppResult.Error(ValidationError.AssistanceSearchedNeighborhoodName.IsNullOrBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
