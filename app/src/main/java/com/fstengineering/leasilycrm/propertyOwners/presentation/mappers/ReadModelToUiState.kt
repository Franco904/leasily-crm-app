package com.fstengineering.leasilycrm.propertyOwners.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.PropertyOwnerUiState
import com.fstengineering.leasilycrm.propertyOwners.application.models.dtos.PropertyOwnerReadModel

fun PropertyOwnerReadModel.toUiState() = PropertyOwnerUiState(
    id = id,
    name = name,
)
