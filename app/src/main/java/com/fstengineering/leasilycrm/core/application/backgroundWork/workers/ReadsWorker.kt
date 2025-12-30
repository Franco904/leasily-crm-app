package com.fstengineering.leasilycrm.core.application.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError

class ReadsWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val domainTablesRepository: DomainTablesRepository,
    private val userRepository: UserRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        domainTablesRepository.getDomainTables()
            .onError { return Result.retry() }

        return Result.success()
    }

    companion object {
        const val UNIQUE_NAME = "ReadsWorker"
    }
}
