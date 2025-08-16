package com.fstengineering.leasilycrm.profile.models

import com.fstengineering.leasilycrm.core.domain.models.User

data class UserUiState(
    val profilePictureUrl: String? = null,
    val name: String? = null,
    val creciNumber: String? = null,
) {
    fun toDomain() = User(
        name = name,
        creciNumber = creciNumber,
        profilePictureUrl = profilePictureUrl,
    )

    companion object {
        fun fromDomain(user: User): UserUiState {
            return UserUiState(
                profilePictureUrl = user.profilePictureUrl,
                name = user.name,
                creciNumber = user.creciNumber,
            )
        }
    }
}
