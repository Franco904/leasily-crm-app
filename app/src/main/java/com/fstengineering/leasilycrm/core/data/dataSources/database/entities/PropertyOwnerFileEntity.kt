package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "property_owner_files",
    foreignKeys = [
        ForeignKey(
            entity = PropertyOwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("property_owner_id")],
)
data class PropertyOwnerFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "property_owner_file_uri") val propertyOwnerFileUri: String,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Int
)
