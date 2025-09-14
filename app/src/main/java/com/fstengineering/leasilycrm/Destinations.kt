package com.fstengineering.leasilycrm

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object Root

    @Serializable
    data object SignIn

    @Serializable
    data object Home {
        @Serializable
        data object Dashboard

        @Serializable
        data object Leads

        @Serializable
        data object Tenants

        @Serializable
        data object Stays
    }

    @Serializable
    data object Profile

    @Serializable
    data object CustomerSegments

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
    data object PropertyOwners

    @Serializable
    data class PropertyOwnerRegistration(val propertyOwnerId: Long?)

    @Serializable
    data class LeadTenantRegistration(val leadTenantId: Long?)

    @Serializable
    data class PropertyOwnerDetails(val propertyOwnerId: Long)
}
