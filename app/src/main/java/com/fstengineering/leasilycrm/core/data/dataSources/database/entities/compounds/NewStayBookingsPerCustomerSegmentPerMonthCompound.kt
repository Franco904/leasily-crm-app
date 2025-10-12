package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class NewStayBookingsPerCustomerSegmentPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "customer_segment_name") val customerSegmentName: String,
    @ColumnInfo(name = "total") val bookingsCount: Int,
)
