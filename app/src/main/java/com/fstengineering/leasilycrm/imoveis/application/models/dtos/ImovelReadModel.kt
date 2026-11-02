package com.fstengineering.leasilycrm.imoveis.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class ImovelReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
)