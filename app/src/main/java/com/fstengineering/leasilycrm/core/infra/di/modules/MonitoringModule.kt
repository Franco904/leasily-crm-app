package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.MonitoringAdapterImpl
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.MonitoringAdapter
import org.koin.dsl.module

val monitoringModule = module {
    single<MonitoringAdapter> { MonitoringAdapterImpl() }
}
