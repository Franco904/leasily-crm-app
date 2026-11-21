package com.fstengineering.leasilycrm.clienteDetails.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class AssistanceSentImovelReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val type: ImovelType,
)
