package com.fstengineering.leasilycrm.core.presentation.models

import android.net.Uri
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class ImovelCardUiState(
    val id: Long,
    val mainPictureUri: Uri?,
    val neighborhood: String,
    val streetAddressAndNumber: String,
    val type: ImovelType,
    val seaProximityTypeId: Long? = null,
    val distanceToSea: String? = null,
    val bedroomsCount: Int? = null,
    val occupiedDates: ImmutableSet<AppDate> = persistentSetOf(),
)
