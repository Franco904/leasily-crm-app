package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile

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
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Int,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    fun toDomain() = PropertyOwnerFile(
        id = id,
        propertyOwnerFileUri = propertyOwnerFileUri,
        propertyOwnerId = propertyOwnerId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyOwnerFile): PropertyOwnerFileEntity {
            requireNotNull(domain.id) { "PropertyOwnerFile.id is required" }
            requireNotNull(domain.propertyOwnerFileUri) { "PropertyOwnerFile.propertyOwnerFileUri is required" }
            requireNotNull(domain.propertyOwnerId) { "PropertyOwnerFile.propertyOwnerId is required" }

            return PropertyOwnerFileEntity(
                id = domain.id,
                propertyOwnerFileUri = domain.propertyOwnerFileUri,
                propertyOwnerId = domain.propertyOwnerId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
