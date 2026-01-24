package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator

class PropertyValidatorImpl : PropertyValidator {
    override fun validate(
        property: Property,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validatePropertyOwner(property.owner).onError { e -> errors.add(e) }
        validatePropertyType(property.type).onError { e -> errors.add(e) }
        validateNeighborhood(property.neighborhood).onError { e -> errors.add(e) }
        validateStreetAddress(property.streetAddress).onError { e -> errors.add(e) }

        validateDistanceToSea(
            seaProximityTypeId = property.seaProximityType?.id,
            distanceToSea = property.distanceToSea,
        ).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validatePropertyOwner(
        owner: PropertyOwner?,
    ): AppResult<Unit, DomainError.PropertyPropertyOwner> {
        return when {
            owner == null -> {
                AppResult.Error(DomainError.PropertyPropertyOwner.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePropertyType(
        type: PropertyType?,
    ): AppResult<Unit, DomainError.PropertyType> {
        return when {
            type == null -> {
                AppResult.Error(DomainError.PropertyType.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateNeighborhood(
        neighborhood: String?,
    ): AppResult<Unit, DomainError.PropertyNeighborhood> {
        return when {
            neighborhood.isNullOrBlank() -> {
                AppResult.Error(DomainError.PropertyNeighborhood.IsBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateStreetAddress(
        streetAddress: String?,
    ): AppResult<Unit, DomainError.PropertyStreetAddress> {
        return when {
            streetAddress.isNullOrBlank() -> {
                AppResult.Error(DomainError.PropertyStreetAddress.IsBlank)
            }

            else -> AppResult.Success(Unit)
        }
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
