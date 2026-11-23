package com.fstengineering.leasilycrm.testUtils.fakes

import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

/** Relógio controlado pelo teste: `now()` devolve [currentDateTime]. */
class FakeAppDateTimeProvider(
    var currentDateTime: AppDateTime = AppDateTimeProviderImpl().now(),
) : AppDateTimeProvider {
    override fun now(): AppDateTime = currentDateTime
}
