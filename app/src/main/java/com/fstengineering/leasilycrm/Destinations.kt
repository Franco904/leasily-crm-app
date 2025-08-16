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
}
