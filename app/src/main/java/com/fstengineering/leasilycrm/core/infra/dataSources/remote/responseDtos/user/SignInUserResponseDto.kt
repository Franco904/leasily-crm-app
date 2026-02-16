package com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.data.models.SyncState
import com.fstengineering.leasilycrm.core.domain.entities.User
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
        inconsistencies = null,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}
