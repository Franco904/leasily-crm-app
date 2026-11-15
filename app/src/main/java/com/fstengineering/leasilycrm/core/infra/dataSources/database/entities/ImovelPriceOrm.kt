package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

@Entity(
    tableName = "property_prices",
    foreignKeys = [
        ForeignKey(
            entity = ImovelOrm::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [Index("property_id")],
)
data class ImovelPriceOrm(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "start_date") val startDate: AppDate,
    @ColumnInfo(name = "end_date") val endDate: AppDate,
    val amount: AppDecimal,
    @ColumnInfo(name = "property_id") val imovelId: Long,
)
