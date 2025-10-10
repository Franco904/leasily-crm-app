package com.fstengineering.leasilycrm.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun rememberNow(interval: Duration = 1.minutes): State<AppLazyDateTime> {
    val now = remember { mutableStateOf(AppLazyDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = AppLazyDateTime.now()
            delay(interval)
        }
    }

    return now
}
