package com.fstengineering.leasilycrm.core.domain.models.wrappers

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class AppDateTime(val dateTime: () -> LocalDateTime) : Comparable<AppDateTime> {
    val year get() = dateTime().year
    val month get() = dateTime().monthValue
    val dayOfMonth get() = dateTime().dayOfMonth
    val hour get() = dateTime().hour
    val minute get() = dateTime().minute
    val second get() = dateTime().second

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

    fun minusDays(daysToSubtract: Long): AppDateTime {
        val updatedDateTime = dateTime().minusDays(daysToSubtract)
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
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun toHourMinute(): Pair<Int, Int> {
        val dateTime = dateTime()
        return dateTime.hour to dateTime.minute
    }

    fun toDate(): AppDate {
        return AppDate { dateTime().toLocalDate() }
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

    override fun compareTo(other: AppDateTime): Int {
        return dateTime().compareTo(other.dateTime())
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

fun Long.toDateTime() = AppDateTime.fromMillis(millis = this)
