package com.fstengineering.leasilycrm.core.application.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold

class BootstrappingWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val domainTablesRepository: DomainTablesRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        return domainTablesRepository.getDomainTables()
            .fold(
                onError = { Result.failure() },
                onSuccess = { Result.success() },
            )
    }

    companion object {
        val uniqueName: String = BootstrappingWorker::class.java.name
    }
}
