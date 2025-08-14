package com.fstengineering.leasilycrm.core.application.di

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.modules.databaseModule
import com.fstengineering.leasilycrm.core.application.di.modules.preferencesModule
import com.fstengineering.leasilycrm.core.application.di.modules.remoteBackendModule
import com.fstengineering.leasilycrm.core.application.di.modules.repositoryModule
import com.fstengineering.leasilycrm.core.application.di.modules.validatorModule
import com.fstengineering.leasilycrm.core.application.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)

        modules(
            databaseModule,
            preferencesModule,
            remoteBackendModule,
            repositoryModule,
            validatorModule,
            viewModelModule,
        )
    }
}
