package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class OccupiedPeriodListProjection(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long?,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: AppDateTime,
    @ColumnInfo(name = "is_auto_check_in") val isAutoCheckIn: Boolean?,
    @ColumnInfo(name = "is_auto_check_out") val isAutoCheckOut: Boolean?,
    @ColumnInfo(name = "status") val status: Int?,
    @ColumnInfo(name = "tenant_name") val tenantName: String?,
    @ColumnInfo(name = "property_type_id") val propertyTypeId: Long?,
    @ColumnInfo(name = "neighborhood") val neighborhood: String?,
)
