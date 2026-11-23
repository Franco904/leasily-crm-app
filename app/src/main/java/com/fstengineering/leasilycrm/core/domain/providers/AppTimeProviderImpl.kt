package com.fstengineering.leasilycrm.core.domain.providers

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppTimeProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppTime
import java.time.LocalTime

class AppTimeProviderImpl : AppTimeProvider {
    override fun now(): AppTime {
        val now = LocalTime.now()
        return AppTime { now }
    }
}
