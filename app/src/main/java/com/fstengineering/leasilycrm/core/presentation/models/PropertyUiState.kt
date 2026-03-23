package com.fstengineering.leasilycrm.core.presentation.models

import android.net.Uri
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class PropertyUiState(
    val id: Long,
    val mainPictureUri: Uri?,
    val neighborhood: String,
    val streetAddressAndNumber: String,
    val type: PropertyType,
    val seaProximityTypeId: Long?,
    val distanceToSea: String?,
    val bedroomsCount: Int,
)
