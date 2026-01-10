package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.StayFile

@Entity(
    tableName = "stay_files",
    foreignKeys = [
        ForeignKey(
            entity = StayEntity::class,
            parentColumns = ["id"],
            childColumns = ["stay_id"],
        ),
    ],
    indices = [Index("stay_id")],
)
data class StayFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "stay_file_uri") val stayFileUri: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "stay_id") val stayId: Long,
) {
    fun toDomain() = StayFile(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        uriString = stayFileUri,
        isDeleted = isDeleted,
        stayId = stayId,
    )

    companion object {
        fun fromDomain(domain: StayFile): StayFileEntity {
            requireNotNull(domain.uriString) { "StayFile.uriString is required" }
            requireNotNull(domain.stayId) { "StayFile.stayId is required" }

            return StayFileEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                stayFileUri = domain.uriString,
                isDeleted = domain.isDeleted,
                stayId = domain.stayId,
            )
        }
    }
}
