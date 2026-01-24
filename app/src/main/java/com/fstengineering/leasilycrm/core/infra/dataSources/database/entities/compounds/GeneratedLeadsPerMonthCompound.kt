package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class GeneratedLeadsPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "total") val leadCount: Int,
)
