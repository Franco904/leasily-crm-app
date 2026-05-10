package com.fstengineering.leasilycrm.core.domain.providers

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

class AppDateProviderImpl : AppDateProvider {
    override fun now(): AppDate = AppDate.now()
}
