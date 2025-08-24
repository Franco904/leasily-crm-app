package com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted

import com.fstengineering.leasilycrm.core.domain.models.User
import io.ktor.http.Cookie
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedPreferences(
    val sessionCookie: Cookie? = null,
    val remoteId: Int? = null,
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userSyncStateId: Int? = null,
) {
    fun toUser(): User {
        return User(
            remoteId = remoteId,
            name = name,
            creciNumber = creciNumber,
            profilePictureUrl = profilePictureUrl,
            userSyncStateId = userSyncStateId,
        )
    }
}
