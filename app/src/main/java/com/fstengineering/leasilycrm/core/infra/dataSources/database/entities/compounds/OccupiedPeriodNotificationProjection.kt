package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class OccupiedPeriodNotificationProjection(
    @ColumnInfo(name = "tenant_name") val tenantName: String,
    @ColumnInfo(name = "property_type_id") val propertyTypeId: Long,
    @ColumnInfo(name = "property_owner_name") val propertyOwnerName: String,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: AppDateTime,
    @ColumnInfo(name = "is_check_in_today") val isCheckInToday: Boolean,
    @ColumnInfo(name = "is_check_out_today") val isCheckOutToday: Boolean,
)
