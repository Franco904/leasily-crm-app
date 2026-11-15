package com.fstengineering.leasilycrm.imoveis.infra.projections

data class ImovelCardProjection(
    val id: Long,
    val typeId: Long,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
)
