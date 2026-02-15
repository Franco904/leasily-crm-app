package com.fstengineering.leasilycrm.core.infra.di.qualifiers

import org.koin.core.qualifier.named

val encryptedPreferencesQualifier = named("EncryptedPreferences")
val plainPreferencesQualifier = named("PlainPreferences")
