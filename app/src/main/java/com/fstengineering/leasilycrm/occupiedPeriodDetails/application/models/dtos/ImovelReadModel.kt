package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class ImovelReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val type: ImovelType,
)
