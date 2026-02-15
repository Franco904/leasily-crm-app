package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

@Entity(
    tableName = "tenants",
    foreignKeys = [
        ForeignKey(
            entity = TenantContactOriginTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_contact_origin_type_id"],
        ),
    ],
    indices = [
        Index("tenant_contact_origin_type_id"),
        Index("user_id"),
    ],
)
data class TenantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val tag: String?,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "tenant_contact_origin_type_id") val tenantContactOriginTypeId: Long?,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    companion object {
        fun fromDomain(domain: Tenant) =
            TenantEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                tag = domain.tag,
                name = domain.name,
                city = domain.city,
                identityDocument = domain.identityDocument,
                phone = domain.phone,
                email = domain.email,
                observations = domain.observations,
                isDeleted = domain.isDeleted,
                tenantContactOriginTypeId = domain.tenantContactOriginType?.id,
                userId = domain.userId,
            )
    }
}
