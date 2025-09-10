package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "stay_file_uri") val stayFileUri: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "stay_id") val stayId: Long,
)
