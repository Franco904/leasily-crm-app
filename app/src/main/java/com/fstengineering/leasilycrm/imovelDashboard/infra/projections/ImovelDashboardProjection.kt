package com.fstengineering.leasilycrm.imovelDashboard.infra.projections

data class ImovelDashboardProjection(
    val id: Long,
    val proprietarioId: Long,
    val typeId: Long,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val mainPictureUriString: String?,
)
