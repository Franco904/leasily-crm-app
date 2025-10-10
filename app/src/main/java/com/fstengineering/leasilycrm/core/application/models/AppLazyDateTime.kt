package com.fstengineering.leasilycrm.core.application.models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class AppLazyDateTime(
    val dateTime: () -> LocalDateTime,
) {
    fun withHourMinute(hour: Int, minute: Int): AppLazyDateTime {
        val updatedDateTime = dateTime().withHour(hour).withMinute(minute)
        return copy(dateTime = { updatedDateTime })
    }

    fun toStartOfDay(): AppLazyDateTime {
        val updatedDateTime = dateTime().withHour(0).withMinute(0)
        return copy(dateTime = { updatedDateTime })
    }

    fun plusDays(daysToAdd: Long): AppLazyDateTime {
        val updatedDateTime = dateTime().plusDays(daysToAdd)
        return copy(dateTime = { updatedDateTime })
    }

    fun toEpochMillisUTC(): Long {
        return dateTime()
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun toEpochMillisLocalZone(): Long {
        return dateTime()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun toHourMinute(): Pair<Int, Int> {
        val dateTime = dateTime()
        return dateTime.hour to dateTime.minute
    }

    fun isAfter(other: AppLazyDateTime?): Boolean {
        return other == null || dateTime() > other.dateTime()
    }

    fun isBefore(other: AppLazyDateTime?): Boolean {
        return other == null || dateTime() < other.dateTime()
    }

    fun isToday(): Boolean {
        val dateTime = dateTime()
        val todayDateTime = now()

        return dateTime.dayOfMonth == todayDateTime.dateTime().dayOfMonth
                && dateTime.month == todayDateTime.dateTime().month
                && dateTime.year == todayDateTime.dateTime().year
    }

    companion object {
        fun now(): AppLazyDateTime = AppLazyDateTime { LocalDateTime.now() }

        fun fromMillis(millis: Long): AppLazyDateTime {
            val lazyDateTime = {
                Instant
                    .ofEpochMilli(/* epochMilli = */ millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime()
            }

            return AppLazyDateTime(dateTime = lazyDateTime)
        }
    }
}
