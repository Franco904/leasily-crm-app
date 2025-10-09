package com.fstengineering.leasilycrm.core.application.models

import java.time.LocalDate

data class AppLazyDate(
    val date: () -> LocalDate,
) {
    companion object {
        fun now(): AppLazyDate = AppLazyDate { LocalDate.now() }
    }
}
