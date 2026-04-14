package com.fstengineering.leasilycrm.core.infra.backgroundWork.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.notifications.TodayCheckInCheckOutNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayCheckInCheckOutNotificationBroadcastReceiver : BroadcastReceiver(),
    KoinComponent {
    private val getTodayCheckInsCheckOutsToNotify: GetTodayCheckInsCheckOutsToNotifyUseCase by inject()

    private val applicationScope: CoroutineScope by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_SHOW_TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION) return

        applicationScope.launch {
            val todayCheckInsCheckOutsToNotify = getTodayCheckInsCheckOutsToNotify()
                .fold(onError = { emptyList() }, onSuccess = { it })

            todayCheckInsCheckOutsToNotify.forEach {
                if (!it.mustNotifyCheckIn && !it.mustNotifyCheckOut) return@forEach

                val notification = TodayCheckInCheckOutNotification.buildNotification(
                    context = context,
                    status = it,
                )

                try {
                    with(NotificationManagerCompat.from(context)) {
                        notify(TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION_ID, notification)
                    }
                } catch (e: SecurityException) {
                    if (BuildConfig.DEBUG) e.printStackTrace()
                }
            }
        }
    }

    companion object {
        const val ACTION_SHOW_TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION =
            "show_today_check_in_check_out_notification"

        const val TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION_ID = 1001
    }
}
