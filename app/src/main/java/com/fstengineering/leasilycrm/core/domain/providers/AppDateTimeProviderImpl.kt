package com.fstengineering.leasilycrm.core.domain.providers

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

class AppDateTimeProviderImpl : AppDateTimeProvider {
    override val now: () -> AppDateTime = { AppDateTime.now() }
}
