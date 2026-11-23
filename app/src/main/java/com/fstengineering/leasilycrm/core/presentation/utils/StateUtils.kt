package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import org.koin.compose.koinInject
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun rememberNow(interval: Duration = 1.minutes): State<AppDateTime> {
    val dateTimeProvider: AppDateTimeProvider = koinInject()
    val now = remember { mutableStateOf(dateTimeProvider.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = dateTimeProvider.now()
            delay(interval)
        }
    }

    return now
}
