package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun rememberNow(interval: Duration = 1.minutes): State<AppDateTime> {
    val now = remember { mutableStateOf(AppDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = AppDateTime.now()
            delay(interval)
        }
    }

    return now
}
