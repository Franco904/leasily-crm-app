package com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain

import kotlinx.serialization.Serializable

@Serializable
data class PlainPreferences(
    val didUserExplicitlySignOut: Boolean? = null,
    val hasSessionExpired: Boolean? = null,
    val leadsOrderTypeId: Int? = null,
    val tenantsOrderTypeId: Int? = null,
    val mustNotShowCheckOutDialogAnymore: Boolean? = null,
    val propertyStaysSelectedPeriodStartMonth: Int? = null,
    val propertyStaysSelectedPeriodStartYear: Int? = null,
    val propertyStaysSelectedPeriodEndMonth: Int? = null,
    val propertyStaysSelectedPeriodEndYear: Int? = null,
    val isTodayCheckInCheckOutNotificationEnabled: Boolean? = null,
)
