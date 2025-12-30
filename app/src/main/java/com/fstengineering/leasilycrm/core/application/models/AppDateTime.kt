package com.fstengineering.leasilycrm.core.application.models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class AppDateTime(
    val dateTime: () -> LocalDateTime,
) {
    fun withHourMinute(hour: Int, minute: Int): AppDateTime {
        val updatedDateTime = dateTime().withHour(hour).withMinute(minute)
        return copy(dateTime = { updatedDateTime })
    }

    fun toStartOfDay(): AppDateTime {
        val updatedDateTime = dateTime().withHour(0).withMinute(0)
        return copy(dateTime = { updatedDateTime })
    }

    fun plusDays(daysToAdd: Long): AppDateTime {
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

    fun isAfter(other: AppDateTime?): Boolean {
        return other == null || dateTime() > other.dateTime()
    }

    fun isBefore(other: AppDateTime?): Boolean {
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
        fun now(): AppDateTime {
            val now = LocalDateTime.now()
            return AppDateTime { now }
        }

        fun lazyNow(): AppDateTime = AppDateTime { LocalDateTime.now() }

        fun fromMillis(millis: Long): AppDateTime {
            val lazyDateTime = {
                Instant
                    .ofEpochMilli(/* epochMilli = */ millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime()
            }

            return AppDateTime(dateTime = lazyDateTime)
        }
    }
}
