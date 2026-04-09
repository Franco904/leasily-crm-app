package com.fstengineering.leasilycrm.signin.application.models.dtos

data class SignInResultWriteModel(
    val remoteId: Long,
    val name: String,
    val creci: String?,
    val profilePictureUrl: String,
    val contractVersion: Int,
)