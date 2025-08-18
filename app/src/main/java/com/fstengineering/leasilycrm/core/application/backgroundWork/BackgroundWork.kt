package com.fstengineering.leasilycrm.core.application.backgroundWork

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.LazyWritesWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.ReadsWorker
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

fun WorkManager.enqueueLazyWritesWorker() {
    val workerConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED) // only runs when connected to Wi-Fi
        .build()

    val repeatIntervalMinutes: Long = if (BuildConfig.DEBUG) 15 else 60

    val workRequest = PeriodicWorkRequestBuilder<LazyWritesWorker>(
        repeatIntervalMinutes, TimeUnit.MINUTES,
    )
        .setConstraints(workerConstraints)
        .build()

    enqueueUniquePeriodicWork(
        LazyWritesWorker.UNIQUE_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest,
    )
}

fun WorkManager.enqueueReadsWorker() {
    val workerConstraints = Constraints.Builder()
        .setRequiresCharging(true) // only runs when charging
        .setRequiresBatteryNotLow(true) // only runs when battery is not low
        .setRequiredNetworkType(NetworkType.UNMETERED) // only runs when connected to Wi-Fi
        .build()

    val workRequest = OneTimeWorkRequestBuilder<ReadsWorker>()
        .setConstraints(workerConstraints)
        .build()

    enqueueUniqueWork(
        ReadsWorker.UNIQUE_NAME,
        ExistingWorkPolicy.REPLACE,
        workRequest,
    )
}

fun WorkManager.enqueueSessionExpirationWorker() {
    val workRequest = OneTimeWorkRequestBuilder<ReadsWorker>()
        .setInitialDelay(1, TimeUnit.HOURS)
        .build()

    enqueueUniqueWork(
        ReadsWorker.UNIQUE_NAME,
        ExistingWorkPolicy.REPLACE,
        workRequest,
    )
}
