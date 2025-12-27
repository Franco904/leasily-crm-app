package com.fstengineering.leasilycrm.core.application.di

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.modules.cacheModule
import com.fstengineering.leasilycrm.core.application.di.modules.coroutineModule
import com.fstengineering.leasilycrm.core.application.di.modules.databaseModule
import com.fstengineering.leasilycrm.core.application.di.modules.externalPresentationModule
import com.fstengineering.leasilycrm.core.application.di.modules.preferencesModule
import com.fstengineering.leasilycrm.core.application.di.modules.remoteBackendModule
import com.fstengineering.leasilycrm.core.application.di.modules.repositoryModule
import com.fstengineering.leasilycrm.core.application.di.modules.sharedStorageModule
import com.fstengineering.leasilycrm.core.application.di.modules.useCaseModule
import com.fstengineering.leasilycrm.core.application.di.modules.utilsModule
import com.fstengineering.leasilycrm.core.application.di.modules.validatorModule
import com.fstengineering.leasilycrm.core.application.di.modules.viewModelModule
import com.fstengineering.leasilycrm.core.application.di.modules.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)
        workManagerFactory()

        modules(
            coroutineModule,
            cacheModule,
            databaseModule,
            preferencesModule,
            remoteBackendModule,
            sharedStorageModule,
            repositoryModule,
            validatorModule,
            useCaseModule,
            viewModelModule,
            externalPresentationModule,
            utilsModule,
            workerModule,
        )
    }
}
