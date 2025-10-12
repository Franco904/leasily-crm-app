package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import java.math.BigDecimal

data class CommissionInvoicePerCustomerSegmentPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "customer_segment_name") val customerSegmentName: String,
    @ColumnInfo(name = "commission_invoice") val commissionInvoice: BigDecimal,
)
