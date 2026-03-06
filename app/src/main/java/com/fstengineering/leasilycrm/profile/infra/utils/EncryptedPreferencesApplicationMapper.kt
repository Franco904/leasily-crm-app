package com.fstengineering.leasilycrm.profile.infra.utils

import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.profile.application.models.UserReadModel

fun EncryptedPreferences.toReadModel() = UserReadModel(
    name = requireNotNull(name),
    creci = creciNumber,
    profilePictureUrl = requireNotNull(profilePictureUrl),
)
