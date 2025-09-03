package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator

class PropertyValidatorImpl : PropertyValidator {
    override fun validate(
        property: Property,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validatePropertyOwner(property.owner).onError { e -> errors.add(e) }
        validatePropertyType(property.type).onError { e -> errors.add(e) }
        validateNeighborhood(property.neighborhood).onError { e -> errors.add(e) }
        validateStreetAddress(property.streetAddress).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validatePropertyOwner(
        owner: PropertyOwner?,
    ): AppResult<Unit, ValidationError.PropertyPropertyOwner> {
        return when {
            owner == null -> {
                AppResult.Error(ValidationError.PropertyPropertyOwner.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePropertyType(
        type: PropertyType?,
    ): AppResult<Unit, ValidationError.PropertyType> {
        return when {
            type == null -> {
                AppResult.Error(ValidationError.PropertyType.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateNeighborhood(
        neighborhood: String?,
    ): AppResult<Unit, ValidationError.PropertyNeighborhood> {
        return when {
            neighborhood.isNullOrBlank() -> {
                AppResult.Error(ValidationError.PropertyNeighborhood.IsBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateStreetAddress(
        streetAddress: String?,
    ): AppResult<Unit, ValidationError.PropertyStreetAddress> {
        return when {
            streetAddress.isNullOrBlank() -> {
                AppResult.Error(ValidationError.PropertyStreetAddress.IsBlank)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
