package com.fstengineering.leasilycrm.clienteRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class ClienteReadModel(
    val id: Long,
    val createDateTime: AppDateTime,
    val tag: String?,
    val name: String,
    val city: String?,
    val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val contactOrigin: String?,
    val observations: String?,
    val isLead: Boolean,
)
