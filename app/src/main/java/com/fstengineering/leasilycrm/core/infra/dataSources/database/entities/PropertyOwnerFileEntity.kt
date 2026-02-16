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
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "property_owner_file_uri") val propertyOwnerFileUri: String,
    @ColumnInfo(name = "property_owner_file_name") val propertyOwnerFileName: String,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
) {
    fun toDomain() = PropertyOwnerFile(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        uriString = propertyOwnerFileUri,
        name = propertyOwnerFileName,
        propertyOwnerId = propertyOwnerId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyOwnerFile) =
            PropertyOwnerFileEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                propertyOwnerFileUri = domain.uriString,
                propertyOwnerFileName = domain.name,
                propertyOwnerId = domain.propertyOwnerId,
                isDeleted = domain.isDeleted,
            )
    }
}
