package com.fstengineering.leasilycrm.core.application.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferencesStorage.Companion.encryptedDataStore
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferencesStorage.Companion.plainDataStore
import org.koin.dsl.module

val preferencesModule = module {
    single<DataStore<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        get<Context>().encryptedDataStore
    }

    single<DataStore<PlainPreferences>>(plainPreferencesQualifier) {
        get<Context>().plainDataStore
    }

    single<PreferencesStorage<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        EncryptedPreferencesStorage(dataStore = get(encryptedPreferencesQualifier))
    }

    single<PreferencesStorage<PlainPreferences>>(plainPreferencesQualifier) {
        PlainPreferencesStorage(dataStore = get(plainPreferencesQualifier))
    }
}
