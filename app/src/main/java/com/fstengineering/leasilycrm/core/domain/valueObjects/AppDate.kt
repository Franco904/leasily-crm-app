package com.fstengineering.leasilycrm.core.domain.valueObjects

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

data class AppDate(val date: LocalDate) : Comparable<AppDate> {
    val year get() = date.year
    val month get() = date.monthValue
    val dayOfMonth get() = date.dayOfMonth

    fun withMonthYear(month: Int, year: Int): AppDate {
        return copy(date = date.withMonth(month).withYear(year))
    }

    fun plusDays(daysToAdd: Long): AppDate {
        return copy(date = date.plusDays(daysToAdd))
    }

    fun getLast12Months(): ImmutableList<CalendarMonthYear> {
        val last12MonthsMap = mutableListOf<CalendarMonthYear>()

        repeat(12) { i ->
            val pastDate = date.minusMonths(i.toLong())

            val calendarMonthYear = CalendarMonthYear(
                month = pastDate.monthValue,
                year = pastDate.year,
            )

            last12MonthsMap.add(calendarMonthYear)
        }

        return last12MonthsMap.toPersistentList()
    }

    fun toEpochMillisUTC(): Long {
        return date
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun isAfter(other: AppDate?): Boolean {
        return other == null || date >= other.date
    }

    fun isBefore(other: AppDate?): Boolean {
        return other == null || date <= other.date
    }

    fun daysBetween(other: AppDate): Int {
        return ChronoUnit.DAYS
            .between(date, other.date)
            .toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AppDate) return false

        return date == other.date
    }

    override fun hashCode(): Int = date.hashCode()

    override fun compareTo(other: AppDate): Int {
        return date.compareTo(other.date)
    }

    companion object {
        fun now(): AppDate {
            return AppDate(date = LocalDate.now())
        }

        fun fromMillis(millis: Long): AppDate {
            return AppDate(
                date = Instant
                    .ofEpochMilli(millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
            )
        }

        fun of(
            year: Int,
            month: Int,
            dayOfMonth: Int,
        ): AppDate {
            return AppDate(date = LocalDate.of(year, month, dayOfMonth))
        }
    }
}

fun Long.toDate() = AppDate.fromMillis(millis = this)
