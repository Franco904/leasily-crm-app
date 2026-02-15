package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType

@Entity(tableName = "tenant_contact_origin_types")
data class TenantContactOriginTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
) {
    fun toDomain() = TenantContactOriginType(
        id = id,
        name = name,
        isActive = isActive,
    )

    companion object {
        fun fromDomain(domain: TenantContactOriginType) =
            TenantContactOriginTypeEntity(
                id = domain.id,
                name = domain.name,
                isActive = domain.isActive,
            )
    }
}
