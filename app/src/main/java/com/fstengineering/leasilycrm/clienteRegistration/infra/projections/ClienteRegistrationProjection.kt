package com.fstengineering.leasilycrm.clienteRegistration.infra.projections

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class ClienteRegistrationProjection(
    val id: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
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
