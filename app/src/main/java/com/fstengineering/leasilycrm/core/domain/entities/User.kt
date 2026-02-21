package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.core.domain.valueObjects.Name
import kotlinx.collections.immutable.ImmutableList

class User private constructor(
    val remoteId: Long,
    val name: Name,
    val creci: Creci?,
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
            val name = Name.parse(name)
            val creci = creciNumber?.let { Creci.parse(creciNumber) }

            return User(
                remoteId = remoteId,
                name = name,
                creci = creci,
                profilePictureUrl = profilePictureUrl,
                userSyncStateId = userSyncStateId,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }
    }
}
