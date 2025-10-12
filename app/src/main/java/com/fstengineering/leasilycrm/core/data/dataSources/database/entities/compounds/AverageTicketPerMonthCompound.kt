package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import java.math.BigDecimal

data class AverageTicketPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "average_ticket") val averageTicket: BigDecimal,
)
