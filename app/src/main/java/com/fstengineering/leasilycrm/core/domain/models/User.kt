package com.fstengineering.leasilycrm.core.domain.models

data class User(
    val email: String? = null,
    val name: String? = null,
    val creciNumber: String? = null,
    val profilePictureUrl: String? = null,
    val isUserModifySynced: Boolean? = null,
) {
    fun isNull() = this == User(
        email = null,
        name = null,
        creciNumber = null,
        profilePictureUrl = null,
    )
}
