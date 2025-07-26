package com.fstengineering.leasilycrm

import android.app.Application
import com.fstengineering.leasilycrm.core.application.di.configureDependencyInjection

class LeasilyCrmApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
    }
}
