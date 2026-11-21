package com.fstengineering.leasilycrm.clienteDetails.application.models

import kotlinx.collections.immutable.ImmutableList

data class ClienteReadModel(
    val id: Long,
    val tag: String?,
    val name: String,
    val city: String?,
    val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val contactOrigin: String?,
    val observations: String?,
    val isLead: Boolean,
    val isOld: Boolean,
    val atendimentos: ImmutableList<AtendimentoReadModel>,
    val stays: ImmutableList<StayReadModel>,
)
