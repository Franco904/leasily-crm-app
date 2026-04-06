package com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.mappers

import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerFileReadModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.models.PropertyOwnerFileUiState
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.models.PropertyOwnerUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerReadModel.toUiState() = PropertyOwnerUiState.Loaded(
    name = name,
    phone = phone,
    email = email,
    paymentKey = paymentKey,
    files = files.map { it.toUiState() }.toPersistentList(),
)

fun PropertyOwnerFileReadModel.toUiState() = PropertyOwnerFileUiState(
    uriString = uriString,
    name = name,
)
