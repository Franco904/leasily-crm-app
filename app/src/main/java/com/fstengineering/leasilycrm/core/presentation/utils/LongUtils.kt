package com.fstengineering.leasilycrm.core.presentation.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

fun Long.timestampToLocalDate(): LocalDate =
    Instant.ofEpochMilli(/* epochMilli = */ this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
