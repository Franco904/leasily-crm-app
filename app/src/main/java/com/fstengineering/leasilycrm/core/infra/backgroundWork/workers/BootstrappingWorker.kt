package com.fstengineering.leasilycrm.core.infra.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.utils.fold

class BootstrappingWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        return Result.success()
    }

    companion object {
        val uniqueName: String = BootstrappingWorker::class.java.name
    }
}
