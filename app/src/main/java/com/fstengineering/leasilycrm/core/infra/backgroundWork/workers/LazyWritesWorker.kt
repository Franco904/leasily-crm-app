package com.fstengineering.leasilycrm.core.infra.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.application.repositories.UserRetryInfoCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold

class LazyWritesWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val userRetryInfoCommandRepository: UserRetryInfoCommandRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        return userRetryInfoCommandRepository.retryUpdate()
            .fold(
                onError = { Result.retry() },
                onSuccess = { Result.success() },
            )
    }

    companion object {
        val uniqueName: String = LazyWritesWorker::class.java.name
    }
}
