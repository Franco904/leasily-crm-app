package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "property_files",
    foreignKeys = [
        ForeignKey(
            entity = ImovelOrm::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [Index("property_id")],
)
data class ImovelFileOrm(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "property_file_uri") val imovelFileUri: String,
    @ColumnInfo(name = "is_main_picture") val isMainPicture: Boolean,
    @ColumnInfo(name = "property_id") val imovelId: Long,
)
