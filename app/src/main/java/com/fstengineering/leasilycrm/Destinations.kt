package com.fstengineering.leasilycrm

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    data object Root

    @Serializable
    data object SignIn

    @Serializable
    data object Home

    @Serializable
    data object Profile

    @Serializable
    data object CustomerSegments

    @Serializable
    data object PropertyOwners

    @Serializable
    data class PropertyOwnerRegistration(val propertyOwnerId: Int?)

    @Serializable
    data class PropertyOwnerDetails(val propertyOwnerId: Int)

    @Serializable
    data object Properties

    @Serializable
    data class PropertyRegistration(val propertyId: Int?) {
        @Serializable
        data object PropertyRegistrationStep1

        @Serializable
        data object PropertyRegistrationStep2

        @Serializable
        data object PropertyRegistrationStep3

        @Serializable
        data object PropertyRegistrationStep4
    }
}
