package com.fstengineering.leasilycrm.core.di

import android.app.Application
import com.fstengineering.leasilycrm.core.di.modules.connectivityModule
import com.fstengineering.leasilycrm.core.di.modules.coroutineModule
import com.fstengineering.leasilycrm.core.di.modules.daoModule
import com.fstengineering.leasilycrm.core.di.modules.databaseDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.domainServiceModule
import com.fstengineering.leasilycrm.core.di.modules.inMemoryDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.preferencesDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.presentationModule
import com.fstengineering.leasilycrm.core.di.modules.remoteDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.repositoryModule
import com.fstengineering.leasilycrm.core.di.modules.sharedStorageDataSourceModule
import com.fstengineering.leasilycrm.core.di.modules.useCaseModule
import com.fstengineering.leasilycrm.core.di.modules.validatorModule
import com.fstengineering.leasilycrm.core.di.modules.viewModelModule
import com.fstengineering.leasilycrm.core.di.modules.workerModule
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
