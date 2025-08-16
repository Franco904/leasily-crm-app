package com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain

import kotlinx.serialization.Serializable

@Serializable
data class PlainPreferences(
    val didUserExplicitlySignOut: Boolean? = null,
)
