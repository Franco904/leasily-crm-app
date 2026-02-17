package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class User private constructor(
    val remoteId: Long,
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
    val userSyncStateId: Int,
    val inconsistencies: ImmutableList<DomainError>,
    val contractVersion: Int,
) {
    companion object {
        fun parse(
            remoteId: Long,
            name: String,
            creciNumber: String?,
            profilePictureUrl: String,
            userSyncStateId: Int,
            inconsistencies: ImmutableList<DomainError>,
            contractVersion: Int,
        ): User {
            creciNumber?.let {
                validateCreciNumber(creciNumber)
                    .onError { throw it.asException() }
            }

            return User(
                remoteId = remoteId,
                name = name,
                creciNumber = creciNumber,
                profilePictureUrl = profilePictureUrl,
                userSyncStateId = userSyncStateId,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }

        fun validateCreciNumber(creciNumber: String): AppResult<Unit, DomainError.User> {
            return when {
                creciNumber.length > 8 -> AppResult.Error(DomainError.User.CreciNumberLengthIsGreaterThan8Chars)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
