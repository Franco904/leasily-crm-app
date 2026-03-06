package com.fstengineering.leasilycrm.core.infra.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferencesDataSource.Companion.encryptedDataStore
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferencesDataSource.Companion.plainDataStore
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import org.koin.dsl.module

val preferencesDataSourceModule = module {
    single<DataStore<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        get<Context>().encryptedDataStore
    }

    single<DataStore<PlainPreferences>>(plainPreferencesQualifier) {
        get<Context>().plainDataStore
    }

    single<PreferencesDataSource<EncryptedPreferences>>(encryptedPreferencesQualifier) {
        EncryptedPreferencesDataSource(dataStore = get(encryptedPreferencesQualifier))
    }

    single<PreferencesDataSource<PlainPreferences>>(plainPreferencesQualifier) {
        PlainPreferencesDataSource(dataStore = get(plainPreferencesQualifier))
    }
}
