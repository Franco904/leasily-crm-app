package com.fstengineering.leasilycrm.profile.infra.mappers

import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.profile.application.models.dtos.UserReadModel

fun EncryptedPreferences.toReadModel(): UserReadModel? {
    if (name == null || profilePictureUrl == null) return null

    return UserReadModel(
        name = name,
        creci = creciNumber,
        profilePictureUrl = profilePictureUrl,
    )
}
