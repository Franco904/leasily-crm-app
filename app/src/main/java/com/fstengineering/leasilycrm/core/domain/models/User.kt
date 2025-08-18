package com.fstengineering.leasilycrm.core.domain.models

data class User(
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userSyncStateId: Int? = null,
) {
    fun isNull() = this == User(
        name = null,
        creciNumber = null,
        profilePictureUrl = null,
    )
}
