package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile

@Entity(
    tableName = "property_files",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [Index("property_id")],
)
data class PropertyFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "property_file_uri") val propertyFileUri: String,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "is_main_picture") val isMainPicture: Boolean = false,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    fun toDomain() = PropertyFile(
        id = id,
        uriString = propertyFileUri,
        propertyId = propertyId,
        isMainPicture = isMainPicture,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyFile): PropertyFileEntity {
            requireNotNull(domain.uriString) { "PropertyFile.uriString is required" }
            requireNotNull(domain.propertyId) { "PropertyFile.propertyId is required" }

            return PropertyFileEntity(
                id = domain.id,
                propertyFileUri = domain.uriString,
                propertyId = domain.propertyId,
                isMainPicture = domain.isMainPicture,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
