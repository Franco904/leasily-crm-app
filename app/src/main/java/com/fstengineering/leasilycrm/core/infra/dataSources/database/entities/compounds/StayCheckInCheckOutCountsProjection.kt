package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class StayCheckInCheckOutCountsProjection(
    @ColumnInfo(name = "stays_able_to_check_in_count") val staysAbleToCheckInCount: Int,
    @ColumnInfo(name = "stays_able_to_check_out_count") val staysAbleToCheckOutCount: Int,
)
