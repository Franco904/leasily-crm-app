package com.fstengineering.leasilycrm.core.application.di

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.modules.connectivityModule
import com.fstengineering.leasilycrm.core.application.di.modules.coroutineModule
import com.fstengineering.leasilycrm.core.application.di.modules.daoModule
import com.fstengineering.leasilycrm.core.application.di.modules.databaseDataSourceModule
import com.fstengineering.leasilycrm.core.application.di.modules.domainServiceModule
import com.fstengineering.leasilycrm.core.application.di.modules.inMemoryDataSourceModule
import com.fstengineering.leasilycrm.core.application.di.modules.preferencesDataSourceModule
import com.fstengineering.leasilycrm.core.application.di.modules.presentationModule
import com.fstengineering.leasilycrm.core.application.di.modules.remoteDataSourceModule
import com.fstengineering.leasilycrm.core.application.di.modules.repositoryModule
import com.fstengineering.leasilycrm.core.application.di.modules.sharedStorageDataSourceModule
import com.fstengineering.leasilycrm.core.application.di.modules.useCaseModule
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
            presentationModule,
            viewModelModule,
            useCaseModule,
            validatorModule,
            domainServiceModule,
            repositoryModule,
            databaseDataSourceModule,
            inMemoryDataSourceModule,
            preferencesDataSourceModule,
            remoteDataSourceModule,
            sharedStorageDataSourceModule,
            connectivityModule,
            daoModule,
            coroutineModule,
            workerModule,
        )
    }
}
