package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppTimeProvider

/** Relógio do app para composables; fornecido na raiz de composição (`MainActivity`). */
val LocalAppDateProvider = staticCompositionLocalOf<AppDateProvider> {
    error("AppDateProvider não fornecido")
}

val LocalAppDateTimeProvider = staticCompositionLocalOf<AppDateTimeProvider> {
    error("AppDateTimeProvider não fornecido")
}

val LocalAppTimeProvider = staticCompositionLocalOf<AppTimeProvider> {
    error("AppTimeProvider não fornecido")
}
