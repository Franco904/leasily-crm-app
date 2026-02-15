package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import com.fstengineering.leasilycrm.core.domain.models.User
import io.ktor.http.Cookie
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedPreferences(
    val sessionCookie: Cookie? = null,
    val remoteId: Long? = null,
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userSyncStateId: Int? = null,
) {
    fun toUser(): User {
        return User(
            remoteId = requireNotNull(remoteId),
            name = requireNotNull(name),
            creciNumber = creciNumber,
            profilePictureUrl = requireNotNull(profilePictureUrl),
            userSyncStateId = requireNotNull(userSyncStateId),
        )
    }
}
