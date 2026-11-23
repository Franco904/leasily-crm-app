package com.fstengineering.leasilycrm.core.domain.providers.interfaces

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppTime

interface AppTimeProvider {
    fun now(): AppTime
}
