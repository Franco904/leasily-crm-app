package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain

import kotlinx.serialization.Serializable

@Serializable
data class PlainPreferences(
    val leadsOrderTypeId: Int? = null,
    val tenantsOrderTypeId: Int? = null,
    val mustNotShowCheckOutDialogAnymore: Boolean? = null,
    val isTodayCheckInCheckOutNotificationEnabled: Boolean? = null,
)
