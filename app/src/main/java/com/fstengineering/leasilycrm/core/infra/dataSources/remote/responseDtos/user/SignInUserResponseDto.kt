package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.core.domain.entities.User
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class SignInUserResponseDto(
    val id: Long,
    val name: String,
    val creciNumber: String? = null,
    val profilePictureUrl: String,
) {
    fun toDomain() = User.parse(
        remoteId = id,
        name = name,
        creciNumber = creciNumber,
        profilePictureUrl = profilePictureUrl,
        userSyncStateId = SyncState.Synced.id,
        inconsistencies = persistentListOf(),
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}
