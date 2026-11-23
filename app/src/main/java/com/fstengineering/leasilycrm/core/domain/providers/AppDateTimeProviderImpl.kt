package com.fstengineering.leasilycrm.core.domain.providers

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import java.time.LocalDateTime

class AppDateTimeProviderImpl : AppDateTimeProvider {
    override fun now(): AppDateTime = AppDateTime(dateTime = LocalDateTime.now())
}
