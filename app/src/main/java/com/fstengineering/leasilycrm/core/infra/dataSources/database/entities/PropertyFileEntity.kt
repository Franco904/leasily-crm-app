package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

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
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "property_file_uri") val propertyFileUri: String,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "is_main_picture") val isMainPicture: Boolean,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
) {
    fun toDomain() = PropertyFile(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        uriString = propertyFileUri,
        propertyId = propertyId,
        isMainPicture = isMainPicture,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyFile) =
            PropertyFileEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                propertyFileUri = domain.uriString,
                propertyId = domain.propertyId,
                isMainPicture = domain.isMainPicture,
                isDeleted = domain.isDeleted,
            )
    }
}
