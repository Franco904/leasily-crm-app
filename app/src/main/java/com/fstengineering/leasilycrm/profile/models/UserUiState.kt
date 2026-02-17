package com.fstengineering.leasilycrm.profile.models

import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class UserUiState(
    val remoteId: Long,
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
    val userSyncStateId: Int,
    val inconsistencies: ImmutableList<DomainError>,
    val contractVersion: Int,
) {
    fun toDomain() = User.parse(
        remoteId = remoteId,
        name = name,
        creciNumber = creciNumber,
        profilePictureUrl = profilePictureUrl,
        userSyncStateId = userSyncStateId,
        inconsistencies = inconsistencies,
        contractVersion = contractVersion,
    )

    companion object {
        fun fromDomain(user: User): UserUiState {
            return UserUiState(
                remoteId = user.remoteId,
                name = user.name,
                creciNumber = user.creciNumber,
                profilePictureUrl = user.profilePictureUrl,
                userSyncStateId = user.userSyncStateId,
                inconsistencies = user.inconsistencies,
                contractVersion = user.contractVersion,
            )
        }
    }
}
