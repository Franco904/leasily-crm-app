package com.fstengineering.leasilycrm.core.infra.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold

class LazyWritesWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        return userRepository.retryUserUpdate()
            .fold(
                onError = { Result.retry() },
                onSuccess = { Result.success() },
            )
    }

    companion object {
        val uniqueName: String = LazyWritesWorker::class.java.name
    }
}
