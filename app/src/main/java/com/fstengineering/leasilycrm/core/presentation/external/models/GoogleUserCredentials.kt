package com.fstengineering.leasilycrm.core.presentation.external.models

import com.fstengineering.leasilycrm.core.domain.models.User

data class GoogleUserCredentials(
    val idToken: String,
    val name: String? = null,
    val profilePictureUrl: String? = null,
) {
    fun toUser() = User(
        name = name,
        profilePictureUrl = profilePictureUrl,
    )
}
