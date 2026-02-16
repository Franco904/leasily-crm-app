package com.fstengineering.leasilycrm.core.domain.helpers

import java.time.LocalTime

data class AppTime(val time: () -> LocalTime) {
    val hour get() = time().hour
    val minute get() = time().minute

    fun withHourMinute(hour: Int, minute: Int): AppTime {
        val updatedTime = time().withHour(hour).withMinute(minute)
        return copy(time = { updatedTime })
    }

    companion object {
        fun now(): AppTime {
            val now = LocalTime.now()
            return AppTime { now }
        }
    }
}