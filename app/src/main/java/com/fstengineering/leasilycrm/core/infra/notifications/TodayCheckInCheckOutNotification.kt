package com.fstengineering.leasilycrm.core.infra.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.graphics.toColorInt
import com.fstengineering.leasilycrm.MainActivity
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

object TodayCheckInCheckOutNotification {
    private const val CHANNEL_ID = "stays_reminder_channel"
    private const val CHANNEL_NAME = "Lembrete de check-ins e check-outs programados para hoje"
    private const val CHANNEL_DESC =
        "Alerta sobre os check-ins e check-outs programados para hoje às 7h30 AM"
    private const val NOTIFICATION_COLOR = "#00696B"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            /* id = */ CHANNEL_ID,
            /* name = */ CHANNEL_NAME,
            /* importance = */ NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = CHANNEL_DESC }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun buildNotification(
        context: Context,
        status: StayCheckNotificationStatus,
    ): Notification {
        val title = when {
            status.mustNotifyCheckIn -> context.getString(R.string.upcoming_check_in_reminder_notification_title)
            else -> context.getString(R.string.upcoming_check_out_reminder_notification_title)
        }

        val textId =
            if (status.mustNotifyCheckIn) R.string.upcoming_check_in_reminder_notification_description
            else R.string.upcoming_check_out_reminder_notification_description

        val propertyTypeName = when {
            status.propertyTypeId == PropertyType.Apartment.id -> context.getString(R.string.no_label)
            else -> context.getString(R.string.na_label)
        }

        val text = context.getString(
            textId,
            status.tenantName,
            propertyTypeName,
            status.propertyTypeId.lowercase(),
            status.propertyOwnerName,
            status.hourMinute.first.formatToTwoDigitsDateTime(),
            status.hourMinute.second.formatToTwoDigitsDateTime(),
        )

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.NAVIGATE_TO_STAYS_EXTRA, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ notificationIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            /* context = */ context,
            /* channelId = */ CHANNEL_ID,
        )
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.schedule)
            .setColor(NOTIFICATION_COLOR.toColorInt())
            .setColorized(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        return notification
    }
}
