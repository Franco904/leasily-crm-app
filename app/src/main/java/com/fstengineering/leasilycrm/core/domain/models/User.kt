package com.fstengineering.leasilycrm.core.domain.models

data class User(
    val remoteId: Long,
    val name: String,
    val creciNumber: String?,
    val profilePictureUrl: String,
    val userSyncStateId: Int,
)
