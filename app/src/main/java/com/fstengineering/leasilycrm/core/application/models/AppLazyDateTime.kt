package com.fstengineering.leasilycrm.core.application.models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class AppLazyDateTime(
    val dateTime: () -> LocalDateTime,
) {
    fun toEpochMillis(): Long {
        return dateTime()
            .atZone(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    fun toStartOfDay(): AppLazyDateTime {
        val updatedDateTime = dateTime().withHour(0).withMinute(0)
        return copy(dateTime = { updatedDateTime })
    }

    fun isHigherThan(other: AppLazyDateTime?): Boolean {
        return other == null || dateTime() > other.dateTime()
    }

    fun isLowerThan(other: AppLazyDateTime?): Boolean {
        return other == null || dateTime() < other.dateTime()
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
