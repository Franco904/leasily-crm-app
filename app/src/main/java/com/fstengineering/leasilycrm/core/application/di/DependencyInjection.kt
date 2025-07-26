package com.fstengineering.leasilycrm.core.application.di

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.module.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)

        modules(
            databaseModule,
        )
    }
}
