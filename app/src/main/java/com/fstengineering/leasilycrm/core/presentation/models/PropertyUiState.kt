package com.fstengineering.leasilycrm.core.presentation.models

import android.net.Uri
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PropertyUiState(
    val id: Long,
    val mainPictureUri: Uri?,
    val neighborhood: String,
    val streetAddressAndNumber: String,
    val type: PropertyType,
    val seaProximityTypeId: Long? = null,
    val distanceToSea: String? = null,
    val bedroomsCount: Int? = null,
    val staysDates: ImmutableList<Pair<AppDateTime, AppDateTime>> = persistentListOf(),
)
