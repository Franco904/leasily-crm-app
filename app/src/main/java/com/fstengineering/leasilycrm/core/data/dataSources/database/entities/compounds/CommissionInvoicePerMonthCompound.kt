package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.application.models.AppDecimal

data class CommissionInvoicePerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "commission_invoice") val commissionInvoice: AppDecimal,
)
