package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

data class CommissionInvoicePerTenantTagPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "tenant_tag_name") val tenantTagName: String,
    @ColumnInfo(name = "commission_invoice") val commissionInvoice: AppDecimal,
)
