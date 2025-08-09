package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos

import com.fstengineering.leasilycrm.core.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponseDto(
    val email: String,
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
) {
    fun toUser(): User {
        return User(
            email = email,
            name = name,
            creciNumber = creciNumber,
            profilePictureUrl = profilePictureUrl,
        )
    }
}
