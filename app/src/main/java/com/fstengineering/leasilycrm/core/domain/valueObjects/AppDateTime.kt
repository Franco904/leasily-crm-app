package com.fstengineering.leasilycrm.core.domain.valueObjects

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class AppDateTime(val dateTime: LocalDateTime) : Comparable<AppDateTime> {
    val year get() = dateTime.year
    val month get() = dateTime.monthValue
    val dayOfMonth get() = dateTime.dayOfMonth
    val hour get() = dateTime.hour
    val minute get() = dateTime.minute
    val second get() = dateTime.second

    fun withHourMinute(hour: Int, minute: Int): AppDateTime {
        return copy(dateTime = dateTime.withHour(hour).withMinute(minute))
    }

    fun toStartOfDay(): AppDateTime {
        return copy(dateTime = dateTime.withHour(0).withMinute(0))
    }

    fun plusDays(daysToAdd: Long): AppDateTime {
        return copy(dateTime = dateTime.plusDays(daysToAdd))
    }

    fun minusDays(daysToSubtract: Long): AppDateTime {
        return copy(dateTime = dateTime.minusDays(daysToSubtract))
    }

    fun toEpochMillisUTC(): Long {
        return dateTime
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun toEpochMillisLocalZone(): Long {
        return dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun toHourMinute(): Pair<Int, Int> {
        return dateTime.hour to dateTime.minute
    }

    fun toDate(): AppDate {
        return AppDate(date = dateTime.toLocalDate())
    }

    fun isAfter(other: AppDateTime?): Boolean {
        return other == null || dateTime > other.dateTime
    }

    fun isBefore(other: AppDateTime?): Boolean {
        return other == null || dateTime < other.dateTime
    }

    fun isToday(): Boolean {
        val todayDateTime = now()

        return dateTime.dayOfMonth == todayDateTime.dateTime.dayOfMonth
                && dateTime.month == todayDateTime.dateTime.month
                && dateTime.year == todayDateTime.dateTime.year
    }

    fun isSameDayAs(other: AppDateTime): Boolean {
        return dateTime.toLocalDate() == other.dateTime.toLocalDate()
    }

    fun isAfterDayOf(other: AppDateTime): Boolean {
        return dateTime.toLocalDate() > other.dateTime.toLocalDate()
    }

    override fun compareTo(other: AppDateTime): Int {
        return dateTime.compareTo(other.dateTime)
    }

    companion object {
        fun now(): AppDateTime {
            return AppDateTime(dateTime = LocalDateTime.now())
        }

        fun fromMillis(millis: Long): AppDateTime {
            return AppDateTime(
                dateTime = Instant
                    .ofEpochMilli(millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime()
            )
        }
    }
}

fun Long.toDateTime() = AppDateTime.fromMillis(millis = this)
