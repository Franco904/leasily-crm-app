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
}
