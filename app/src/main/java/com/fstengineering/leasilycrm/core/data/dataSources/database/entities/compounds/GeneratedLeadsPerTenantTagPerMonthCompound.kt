package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class GeneratedLeadsPerTenantTagPerMonthCompound(
    @ColumnInfo(name = "month_year") val monthYear: String,
    @ColumnInfo(name = "tenant_tag_name") val tenantTagName: String,
    @ColumnInfo(name = "total") val leadCount: Int,
)
