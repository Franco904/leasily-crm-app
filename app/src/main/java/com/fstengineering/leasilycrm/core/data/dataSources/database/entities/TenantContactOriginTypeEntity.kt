package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType

@Entity(tableName = "tenant_contact_origin_types")
data class TenantContactOriginTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
) {
    companion object {
        fun fromDomain(domain: TenantContactOriginType) = TenantContactOriginTypeEntity(
            id = domain.id,
            name = domain.name,
        )
    }
}
