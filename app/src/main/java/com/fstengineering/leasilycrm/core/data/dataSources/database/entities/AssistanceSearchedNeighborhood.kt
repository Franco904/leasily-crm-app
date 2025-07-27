package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assistance_searched_neighborhoods",
    foreignKeys = [
        ForeignKey(
            entity = Assistance::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("assistance_id")],
)
data class AssistanceSearchedNeighborhood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val neighborhood: String,
    @ColumnInfo(name = "assistance_id") val assistanceId: Int,
)
