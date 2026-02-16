package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

data class CommissionInvoicePerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "commission_invoice") val commissionInvoice: AppDecimal,
)
