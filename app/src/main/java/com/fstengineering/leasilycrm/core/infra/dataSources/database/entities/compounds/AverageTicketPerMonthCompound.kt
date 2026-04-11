package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class AverageTicketPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "average_ticket") val averageTicket: AppDecimal,
)
