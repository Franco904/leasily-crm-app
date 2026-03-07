package com.fstengineering.leasilycrm.signin.application.models

data class SignInResultWriteModel(
    val remoteId: Long,
    val name: String,
    val creci: String?,
    val profilePictureUrl: String,
    val contractVersion: Int,
)
