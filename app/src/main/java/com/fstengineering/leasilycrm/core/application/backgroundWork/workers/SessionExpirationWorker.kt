package com.fstengineering.leasilycrm.core.application.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.hasError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class SessionExpirationWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        userRepository.markSessionAsExpired()
            .onError { return Result.failure() }

        userRepository.deleteCachedUser()
            .onError { return Result.failure() }

        return Result.success()
    }

    companion object {
        const val UNIQUE_NAME = "SessionExpirationWorker"
    }
}