package com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class ImovelRawReadModel(
    val id: Long,
    val proprietarioId: Long,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
)
