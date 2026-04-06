package com.fstengineering.leasilycrm.propertyOwners.presentation.mappers

import com.fstengineering.leasilycrm.propertyOwners.application.models.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwners.presentation.models.PropertyOwnerUiState

fun PropertyOwnerReadModel.toUiState() = PropertyOwnerUiState(
    id = id,
    name = name,
)
