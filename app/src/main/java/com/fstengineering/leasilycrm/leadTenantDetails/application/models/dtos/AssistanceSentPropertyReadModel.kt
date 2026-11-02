package com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class AssistanceSentPropertyReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val type: ImovelType,
)
