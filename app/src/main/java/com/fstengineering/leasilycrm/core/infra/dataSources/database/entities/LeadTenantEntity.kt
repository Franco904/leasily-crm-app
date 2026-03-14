package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "lead_tenants",
)
data class LeadTenantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val tag: String?,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "contact_origin") val contactOrigin: String?,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
)
