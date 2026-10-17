package com.fstengineering.leasilycrm.core.infra.backgroundWork.broadcastReceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.notifications.TodayCheckInCheckOutNotification
import com.fstengineering.leasilycrm.core.infra.utils.hasNotificationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayCheckInCheckOutNotificationBroadcastReceiver : BroadcastReceiver(),
    KoinComponent {
    private val settingsSharedStore: SettingsSharedStore by inject()
    private val userSessionSharedStore: UserSessionSharedStore by inject()
    private val occupiedSharedQueryRepository: OccupiedPeriodSharedQueryRepository by inject()

    private val applicationScope: CoroutineScope by inject()

    // Permissão de notificação verificada antes do notify via hasNotificationPermission(); o lint
    // não reconhece a checagem feita fora deste método.
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_SHOW_TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION) return

        applicationScope.launch {
            val isTodayCheckInCheckOutNotificationEnabled =
                settingsSharedStore.watch().firstOrNull()?.isTodayCheckInCheckOutNotificationEnabled
                    ?: false

            val isSessionAlive = userSessionSharedStore.isSessionAlive()

            if (
                AppDateTime.now().hour > 9 ||
                !isTodayCheckInCheckOutNotificationEnabled ||
                !isSessionAlive ||
                !context.hasNotificationPermission()
            ) {
                return@launch
            }

            val todayCheckInsCheckOutsToNotify =
                occupiedSharedQueryRepository.getAllTodayCheckInsAndCheckOutsToNotify()
                    .fold(onError = { emptyList() }, onSuccess = { it })

            todayCheckInsCheckOutsToNotify.forEach {
                val notification = TodayCheckInCheckOutNotification.buildNotification(
                    context = context,
                    status = it,
                )

                NotificationManagerCompat.from(context)
                    .notify(TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION_ID, notification)
            }
        }
    }

    companion object {
        const val ACTION_SHOW_TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION =
            "show_today_check_in_check_out_notification"

        const val TODAY_CHECK_IN_CHECK_OUT_NOTIFICATION_ID = 1001
    }
}
