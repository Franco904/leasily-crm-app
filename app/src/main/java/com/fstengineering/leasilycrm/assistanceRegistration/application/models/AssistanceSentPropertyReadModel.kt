package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import android.net.Uri
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class AssistanceSentPropertyReadModel(
    val id: Long?,
    val type: PropertyType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val seaProximityTypeId: Long?,
    val distanceToSea: String?,
    val bedroomsCount: Int,
    val mainPictureUri: Uri?,
)
