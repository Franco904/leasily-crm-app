package com.fstengineering.leasilycrm.core.infra.backgroundWork.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.fstengineering.leasilycrm.core.infra.backgroundWork.broadcastReceivers.TodayCheckInCheckOutNotificationBroadcastReceiver
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

object TodayCheckInCheckOutNotificationAlarm {
    private const val ALARM_INTENT_REQUEST_CODE = 0

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(
            context,
            TodayCheckInCheckOutNotificationBroadcastReceiver::class.java,
        ).apply {
            action = TodayCheckInCheckOutNotificationBroadcastReceiver
                .ACTION_SHOW_TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ ALARM_INTENT_REQUEST_CODE,
            /* intent = */ receiverIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val now = AppDateTime.now()
        val triggerAtMillis = now.withHourMinute(hour = 7, minute = 0)

        val triggerAtMillisAdjusted = (
                triggerAtMillis.takeIf { it.isAfter(now) }
                    ?: triggerAtMillis.plusDays(daysToAdd = 1)
                )
            .toEpochMillisLocalZone()

        alarmManager.cancel(pendingIntent)
        alarmManager.setInexactRepeating(
            /* type = */ AlarmManager.RTC_WAKEUP,
            /* triggerAtMillis = */ triggerAtMillisAdjusted,
            /* intervalMillis = */ AlarmManager.INTERVAL_DAY,
            /* operation = */ pendingIntent,
        )
    }
}
