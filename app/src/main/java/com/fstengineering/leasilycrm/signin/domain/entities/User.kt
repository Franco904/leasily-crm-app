package com.fstengineering.leasilycrm.signin.domain.entities

data class User(
    val remoteId: Long,
    val name: String,
    val creci: String?,
    val profilePictureUrl: String,
    val contractVersion: Int,
)
