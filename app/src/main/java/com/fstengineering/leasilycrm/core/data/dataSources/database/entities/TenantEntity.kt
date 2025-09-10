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
            entity = CustomerSegmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_segment_id"],
        ),
        ForeignKey(
            entity = TenantContactOriginTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_contact_origin_type_id"],
        ),
    ],
    indices = [
        Index("customer_segment_id"),
        Index("tenant_contact_origin_type_id"),
    ],
)
data class TenantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "register_date") val registerDate: LocalDate,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val observations: String?,
    @ColumnInfo(name = "is_lead") val isLead: Boolean = true,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "customer_segment_id") val customerSegmentId: Long?,
    @ColumnInfo(name = "tenant_contact_origin_type_id") val tenantContactOriginTypeId: Long?,
    @ColumnInfo(name = "user_id") val userId: Long,
)
