package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceValidator

class AssistanceValidatorImpl : AssistanceValidator {
    override fun validate(
        assistance: Assistance,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateDistanceToSea(
            seaProximityTypeId = assistance.seaProximityType?.id,
            distanceToSea = assistance.distanceToSea,
        ).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateDistanceToSea(
        seaProximityTypeId: Long?,
        distanceToSea: AppDecimal?,
    ): AppResult<Unit, DomainError.PropertyDistanceToSea> {
        return when {
            seaProximityTypeId == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID && distanceToSea == null -> {
                AppResult.Error(DomainError.PropertyDistanceToSea.IsBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
