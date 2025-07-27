package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_types")
data class PropertyType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)
