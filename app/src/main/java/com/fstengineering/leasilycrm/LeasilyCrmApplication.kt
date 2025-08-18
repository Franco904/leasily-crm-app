package com.fstengineering.leasilycrm

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.configureDependencyInjection
import org.koin.core.component.KoinComponent

class LeasilyCrmApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
    }
}
