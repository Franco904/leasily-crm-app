package com.fstengineering.leasilycrm

import android.app.Application
import com.fstengineering.leasilycrm.core.di.configureDependencyInjection
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.infra.adapters.CurrentActivityTracker
import com.fstengineering.leasilycrm.core.infra.backgroundWork.alarms.TodayCheckInCheckOutNotificationAlarm
import com.fstengineering.leasilycrm.core.infra.notifications.TodayCheckInCheckOutNotification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LeasilyCrmApplication : Application(), KoinComponent {
    private val dateTimeProvider: AppDateTimeProvider by inject()
    private val currentActivityTracker: CurrentActivityTracker by inject()

    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()

        registerActivityLifecycleCallbacks(currentActivityTracker)

        TodayCheckInCheckOutNotificationAlarm.schedule(
            applicationContext,
            now = dateTimeProvider.now()
        )
        TodayCheckInCheckOutNotification.createNotificationChannel(applicationContext)
    }
}
