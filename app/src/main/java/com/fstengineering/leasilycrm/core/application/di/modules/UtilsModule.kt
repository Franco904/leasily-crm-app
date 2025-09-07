package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.presentation.utils.CalendarSelectionUtilsImpl
import com.fstengineering.leasilycrm.core.presentation.utils.interfaces.CalendarSelectionUtils
import org.koin.dsl.module

val utilsModule = module {
    single<CalendarSelectionUtils> { CalendarSelectionUtilsImpl() }
}
