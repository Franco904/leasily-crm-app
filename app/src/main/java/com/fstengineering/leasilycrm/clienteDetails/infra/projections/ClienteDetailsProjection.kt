package com.fstengineering.leasilycrm.clienteDetails.infra.projections

import androidx.room.ColumnInfo

data class ClienteDetailsProjection(
    val id: Long,
    val tag: String?,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "contact_origin") val contactOrigin: String?,
    val observations: String?,
    val status: Int,
)
