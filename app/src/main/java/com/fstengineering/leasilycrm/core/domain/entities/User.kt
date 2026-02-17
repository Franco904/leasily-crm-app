package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class User private constructor(
    val remoteId: Long,
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
    val userSyncStateId: Int,
    val inconsistencies: String?,
    val contractVersion: Int,
) {
    companion object {
        fun parse(
            remoteId: Long,
            name: String,
            creciNumber: String?,
            profilePictureUrl: String,
            userSyncStateId: Int,
            inconsistencies: String?,
            contractVersion: Int,
        ): User {
            creciNumber?.let { validateCreciNumber(creciNumber) }

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

        private fun validateCreciNumber(creciNumber: String) {
            if (creciNumber.length > 8) throw DomainError.User.CreciNumberLengthIsGreaterThan8Chars.asException()
        }
    }
}
