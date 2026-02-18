package com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import io.ktor.http.Cookie
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedPreferences(
    val sessionCookie: Cookie? = null,
    val remoteId: Long? = null,
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userSyncStateId: Int? = null,
    val userInconsistencies: String? = null,
) {
    fun toUser(): User {
        val inconsistencies = userInconsistencies
            ?.split(",")
            ?.map { DomainError.User.CreciNumberLengthIsGreaterThan8Chars }
            ?.toImmutableList()
            ?: persistentListOf()

        return User.parse(
            remoteId = requireNotNull(remoteId),
            name = requireNotNull(name),
            creciNumber = creciNumber,
            profilePictureUrl = requireNotNull(profilePictureUrl),
            userSyncStateId = requireNotNull(userSyncStateId),
            inconsistencies = inconsistencies,
            contractVersion = BuildConfig.CONTRACT_VERSION,
        )
    }
}
