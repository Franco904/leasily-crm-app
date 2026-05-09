package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import io.ktor.http.Cookie
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedPreferences(
    val sessionCookie: Cookie? = null,
    val remoteId: Long? = null,
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userContractVersion: Int? = null,
    val userSyncStateId: Int? = null,
)
