package com.fstengineering.leasilycrm.core.domain.providers.interfaces

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate

interface AppDateProvider {
    fun now(): AppDate
}
