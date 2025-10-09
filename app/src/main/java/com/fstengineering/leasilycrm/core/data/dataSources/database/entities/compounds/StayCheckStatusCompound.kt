package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.ColumnInfo
import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime
import com.fstengineering.leasilycrm.core.domain.models.StayCheckStatus

data class StayCheckStatusCompound(
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppLazyDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: AppLazyDateTime,
    @ColumnInfo(name = "is_check_in_done") val isCheckInDone: Boolean,
) {
    fun toDomain() = StayCheckStatus(
        checkInDateTime = checkInDateTime,
        checkOutDateTime = checkOutDateTime,
        isCheckInDone = isCheckInDone,
    )
}
