package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.ColumnInfo

data class LeadContactChannelCountCompound(
    @ColumnInfo(name = "lead_contact_channel_name") val leadContactChannelName: String?,
    @ColumnInfo(name = "lead_count") val leadCount: Int,
)
