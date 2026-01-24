package com.fstengineering.leasilycrm.core.infra.backgroundWork

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.backgroundWork.workers.BootstrappingWorker
import com.fstengineering.leasilycrm.core.infra.backgroundWork.workers.LazyWritesWorker
import java.util.concurrent.TimeUnit

fun WorkManager.enqueueBootstrappingWorker() {
    val workerConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED) // only runs when connected to Wi-Fi
        .build()

    val workRequest = OneTimeWorkRequestBuilder<BootstrappingWorker>()
        .setConstraints(workerConstraints)
        .build()

    enqueueUniqueWork(
        uniqueWorkName = BootstrappingWorker.uniqueName,
        existingWorkPolicy = ExistingWorkPolicy.REPLACE,
        request = workRequest,
    )
}

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
        uniqueWorkName = LazyWritesWorker.uniqueName,
        existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
        request = workRequest,
    )
}
