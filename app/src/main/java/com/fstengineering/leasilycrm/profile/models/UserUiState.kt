package com.fstengineering.leasilycrm.profile.models

import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed interface UserUiState {
    data object Empty : UserUiState

    data class Loaded(
        val name: String,
        val creciNumber: String?,
        val profilePictureUrl: String,
    ) : UserUiState {
        companion object {
            fun fromDomain(user: User): Loaded {
                return Loaded(
                    name = user.name,
                    creciNumber = user.creciNumber,
                    profilePictureUrl = user.profilePictureUrl,
                )
            }
        }
    }
}
