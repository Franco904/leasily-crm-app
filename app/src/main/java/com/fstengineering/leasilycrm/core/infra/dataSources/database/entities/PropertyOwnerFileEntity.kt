package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "property_owner_files",
    foreignKeys = [
        ForeignKey(
            entity = PropertyOwnerEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
        ),
    ],
    indices = [Index("property_owner_id")],
)
data class PropertyOwnerFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "property_owner_file_uri") val propertyOwnerFileUri: String,
    @ColumnInfo(name = "property_owner_file_name") val propertyOwnerFileName: String,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Long,
)
