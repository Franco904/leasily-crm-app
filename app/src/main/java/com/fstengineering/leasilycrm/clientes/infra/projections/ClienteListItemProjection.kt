package com.fstengineering.leasilycrm.clientes.infra.projections

data class ClienteListItemProjection(
    val id: Long,
    val name: String,
    val city: String?,
    val hasUnfinishedAssistance: Boolean,
    val hospedesCount: Int?,
    val hasUnfinishedStay: Boolean,
    val currentStayId: Long?,
)
