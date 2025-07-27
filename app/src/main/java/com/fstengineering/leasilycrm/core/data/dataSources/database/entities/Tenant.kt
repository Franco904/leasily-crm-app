package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "tenants",
    foreignKeys = [
        ForeignKey(
            entity = CustomerSegment::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TenantContactOriginType::class,
            parentColumns = ["id"],
            childColumns = ["tenant_contact_origin_type_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("customer_segment_id"),
        Index("tenant_contact_origin_type_id"),
    ],
)
data class Tenant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "register_date") val registerDate: LocalDate,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val observations: String?,
    @ColumnInfo(name = "is_lead") val isLead: Boolean = true,
    @ColumnInfo(name = "customer_segment_id") val customerSegmentId: Int?,
    @ColumnInfo(name = "tenant_contact_origin_type_id") val tenantContactOriginTypeId: Int?,
    @ColumnInfo(name = "user_id") val userId: Int,
)
