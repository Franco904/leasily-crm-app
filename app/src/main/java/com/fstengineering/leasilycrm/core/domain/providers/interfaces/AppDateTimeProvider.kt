package com.fstengineering.leasilycrm.core.domain.providers.interfaces

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

interface AppDateTimeProvider {
    fun now(): AppDateTime
}
