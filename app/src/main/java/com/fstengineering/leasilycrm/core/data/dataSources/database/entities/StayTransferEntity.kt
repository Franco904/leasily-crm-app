package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "stay_transfers",
    foreignKeys = [
        ForeignKey(
            entity = StayEntity::class,
            parentColumns = ["id"],
            childColumns = ["stay_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("stay_id")],
)
data class StayTransferEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "transfer_date") val transferDate: LocalDate,
    @ColumnInfo(name = "transfer_amount") val transferAmount: String,
    @ColumnInfo(name = "transfer_description") val transferDescription: String,
    @ColumnInfo(name = "stay_id") val stayId: Int
)