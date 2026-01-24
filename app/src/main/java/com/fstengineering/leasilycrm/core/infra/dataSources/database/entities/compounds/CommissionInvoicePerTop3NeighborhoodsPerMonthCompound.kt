package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

data class CommissionInvoicePerTop3NeighborhoodsPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "property_neighborhood") val propertyNeighborhood: String,
    @ColumnInfo(name = "commission_invoice") val commissionInvoice: AppDecimal,
)
