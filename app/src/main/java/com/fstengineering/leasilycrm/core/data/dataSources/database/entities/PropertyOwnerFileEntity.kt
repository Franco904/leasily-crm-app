package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile

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
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "property_owner_file_uri") val propertyOwnerFileUri: String,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    fun toDomain() = PropertyOwnerFile(
        id = id,
        uriString = propertyOwnerFileUri,
        propertyOwnerId = propertyOwnerId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyOwnerFile): PropertyOwnerFileEntity {
            requireNotNull(domain.uriString) { "PropertyOwnerFile.propertyOwnerFileUri is required" }
            requireNotNull(domain.propertyOwnerId) { "PropertyOwnerFile.propertyOwnerId is required" }

            return PropertyOwnerFileEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                propertyOwnerFileUri = domain.uriString,
                propertyOwnerId = domain.propertyOwnerId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
