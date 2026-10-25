package com.fstengineering.leasilycrm

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object Root

    @Serializable
    data class SignIn(val hasSessionExpired: Boolean)

    @Serializable
    data class Home(val startedFromStayNotificationPopUp: Boolean) {
        @Serializable
        data object Dashboard

        @Serializable
        data object Leads

        @Serializable
        data object Tenants

        @Serializable
        data object OccupiedPeriods
    }

    @Serializable
    data object Settings

    @Serializable
    data object Profile

    @Serializable
    data object Properties

    @Serializable
    data class PropertyRegistration(val propertyId: Long?) {
        @Serializable
        data object PropertyRegistrationStep1

        @Serializable
        data object PropertyRegistrationStep2

        @Serializable
        data object PropertyRegistrationStep3

        @Serializable
        data object PropertyRegistrationStep4
    }

    @Serializable
    data class PropertyDashboard(val propertyId: Long)

    @Serializable
    data class PropertyDetails(val propertyId: Long)

    @Serializable
    data class PropertyOccupation(val propertyId: Long)

    @Serializable
    data object Proprietarios

    @Serializable
    data class ProprietarioRegistration(val proprietarioId: Long?)

    @Serializable
    data class ProprietarioDetails(val proprietarioId: Long)

    @Serializable
    data class LeadTenantRegistration(val leadTenantId: Long?)

    @Serializable
    data class LeadTenantDetails(val leadTenantId: Long)

    @Serializable
    data class AssistanceRegistration(
        val assistanceId: Long?,
        val leadTenantId: Long,
    ) {
        @Serializable
        data object AssistanceRegistrationStep1

        @Serializable
        data object AssistanceRegistrationStep2

        @Serializable
        data object AssistanceRegistrationStep3
    }

    @Serializable
    data class OccupiedPeriodRegistration(
        val occupiedPeriodId: Long?,
        val propertyId: Long?,
        val leadTenantId: Long?,
        val isUnavailablePeriod: Boolean,
    )

    @Serializable
    data class OccupiedPeriodDetails(
        val occupiedPeriodId: Long,
        val cameFromProperty: Boolean,
        val cameFromTenant: Boolean,
    )
}
