package com.fstengineering.leasilycrm.core.domain.models.wrappers

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

data class AppDate(val date: () -> LocalDate) : Comparable<AppDate> {
    val year get() = date().year
    val month get() = date().monthValue
    val dayOfMonth get() = date().dayOfMonth

    fun withMonthYear(month: Int, year: Int): AppDate {
        val updatedDate = date().withMonth(month).withYear(year)
        return copy(date = { updatedDate })
    }

    fun plusDays(daysToAdd: Long): AppDate {
        val updatedDate = date().plusDays(daysToAdd)
        return copy(date = { updatedDate })
    }

    fun getLast12Months(): ImmutableList<CalendarMonthYear> {
        val last12MonthsMap = mutableListOf<CalendarMonthYear>()

        repeat(12) { i ->
            val pastDate = date().minusMonths(i.toLong())

            val calendarMonthYear = CalendarMonthYear(
                month = pastDate.monthValue,
                year = pastDate.year,
            )

            last12MonthsMap.add(calendarMonthYear)
        }

        return last12MonthsMap.toPersistentList()
    }

    fun toEpochMillisUTC(): Long {
        return date()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun isAfter(other: AppDate?): Boolean {
        return other == null || date() >= other.date()
    }

    fun isBefore(other: AppDate?): Boolean {
        return other == null || date() <= other.date()
    }

    fun daysBetween(other: AppDate): Int {
        return ChronoUnit.DAYS
            .between(date(), other.date())
            .toInt()
    }

    override fun compareTo(other: AppDate): Int {
        return date().compareTo(other.date())
    }

    companion object {
        fun now(): AppDate {
            val now = LocalDate.now()
            return AppDate { now }
        }

        fun lazyNow(): AppDate = AppDate { LocalDate.now() }

        fun fromMillis(millis: Long): AppDate {
            val lazyDate = {
                Instant
                    .ofEpochMilli(/* epochMilli = */ millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
            }

            return AppDate(date = lazyDate)
        }

        fun of(
            year: Int,
            month: Int,
            dayOfMonth: Int,
        ): AppDate {
            return AppDate {
                LocalDate.of(year, month, dayOfMonth)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toLocalDate()
            }
        }

        fun getDatesWithMissingMonthsBetween(
            startDate: AppDate,
            endDate: AppDate,
            staysStructuredDatesMap: Map<CalendarMonthYear, PersistentList<StayCalendarDate>>,
        ): ImmutableMap<CalendarMonthYear, PersistentList<StayCalendarDate>> {
            val staysStructuredDatesWithMissingMonthsMap =
                mutableMapOf<CalendarMonthYear, PersistentList<StayCalendarDate>>()

            var currentDate = startDate.date()
            while (currentDate <= endDate.date()) {
                val calendarMonthYear = CalendarMonthYear(
                    month = currentDate.monthValue,
                    year = currentDate.year,
                )

                staysStructuredDatesWithMissingMonthsMap.put(calendarMonthYear, persistentListOf())
                currentDate = currentDate.plusMonths(1)
            }

            staysStructuredDatesWithMissingMonthsMap.keys.forEach { monthYear ->
                val stayDates = staysStructuredDatesMap[monthYear]

                staysStructuredDatesWithMissingMonthsMap.put(
                    monthYear,
                    stayDates ?: persistentListOf()
                )
            }

            return staysStructuredDatesWithMissingMonthsMap
                .toPersistentMap()
        }
    }
}

fun Long.toDate() = AppDate.fromMillis(millis = this)
