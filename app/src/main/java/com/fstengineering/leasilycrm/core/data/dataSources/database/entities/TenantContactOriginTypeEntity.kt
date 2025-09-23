package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType

@Entity(tableName = "tenant_contact_origin_types")
data class TenantContactOriginTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
) {
    fun toDomain() = TenantContactOriginType(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: TenantContactOriginType): TenantContactOriginTypeEntity {
            requireNotNull(domain.name) { "TenantContactOriginType.name is required" }

            return TenantContactOriginTypeEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
        }
    }
}
