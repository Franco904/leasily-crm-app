package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class NewStayBookingsPerTop3NeighborhoodsPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "property_neighborhood") val propertyNeighborhood: String,
    @ColumnInfo(name = "total") val bookingsCount: Int,
)
