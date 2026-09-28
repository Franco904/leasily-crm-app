package com.fstengineering.leasilycrm.profile.application.models.dtos

data class UserReadModel(
    val name: String,
    val creci: String?,
    val profilePictureUrl: String,
)