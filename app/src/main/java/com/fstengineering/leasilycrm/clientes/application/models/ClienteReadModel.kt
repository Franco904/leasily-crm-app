package com.fstengineering.leasilycrm.clientes.application.models

data class ClienteReadModel(
    val id: Long,
    val name: String,
    val city: String?,
    val hasUnfinishedAssistance: Boolean,
    val hospedesCount: Int?,
    val hasUnfinishedStay: Boolean,
    val currentStayId: Long?,
)
