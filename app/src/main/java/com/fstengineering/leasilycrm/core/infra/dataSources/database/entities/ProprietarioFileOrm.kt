package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "property_owner_files",
    foreignKeys = [
        ForeignKey(
            entity = ProprietarioOrm::class,
            parentColumns = ["id"],
            childColumns = ["property_owner_id"],
        ),
    ],
    indices = [Index("property_owner_id")],
)
data class ProprietarioFileOrm(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "property_owner_file_uri") val proprietarioFileUri: String,
    @ColumnInfo(name = "property_owner_file_name") val proprietarioFileName: String,
    @ColumnInfo(name = "property_owner_id") val proprietarioId: Long,
)
