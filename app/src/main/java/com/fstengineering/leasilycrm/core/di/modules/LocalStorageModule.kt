package com.fstengineering.leasilycrm.core.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedLocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedLocalStorage.Companion.encryptedDataStore
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainLocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainLocalStorage.Companion.plainDataStore
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainPreferences
import org.koin.dsl.module

val localStorageModule = module {
    single<DataStore<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        get<Context>().encryptedDataStore
    }

    single<DataStore<PlainPreferences>>(plainPreferencesQualifier) {
        get<Context>().plainDataStore
    }

    single<LocalStorage<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        EncryptedLocalStorage(dataStore = get(encryptedPreferencesQualifier))
    }

    single<LocalStorage<PlainPreferences>>(plainPreferencesQualifier) {
        PlainLocalStorage(dataStore = get(plainPreferencesQualifier))
    }
}
