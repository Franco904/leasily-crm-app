package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tenant_contact_origin_types")
data class TenantContactOriginType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)
