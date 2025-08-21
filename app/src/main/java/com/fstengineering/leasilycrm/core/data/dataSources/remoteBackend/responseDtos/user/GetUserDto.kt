package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user

import com.fstengineering.leasilycrm.core.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class GetUserDto(
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
) {
    fun toUser(): User {
        return User(
            name = name,
            creciNumber = creciNumber,
            profilePictureUrl = profilePictureUrl,
        )
    }
}