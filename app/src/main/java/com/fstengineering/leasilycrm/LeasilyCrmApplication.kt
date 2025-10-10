package com.fstengineering.leasilycrm

import android.app.Application
import com.fstengineering.leasilycrm.core.application.backgroundWork.alarms.TodayCheckInCheckOutNotificationAlarm
import com.fstengineering.leasilycrm.core.application.di.configureDependencyInjection
import com.fstengineering.leasilycrm.core.presentation.notifications.TodayCheckInCheckOutNotification
import org.koin.core.component.KoinComponent

class LeasilyCrmApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()

        TodayCheckInCheckOutNotificationAlarm.schedule(applicationContext)
        TodayCheckInCheckOutNotification.createNotificationChannel(applicationContext)
    }
}
