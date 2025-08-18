package com.fstengineering.leasilycrm.core.application.backgroundWork.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.hasError

class ReadsWorker(
    applicationContext: Context,
    workerParameters: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(applicationContext, workerParameters) {
    override suspend fun doWork(): Result {
        val result = userRepository.getRemoteUser()

        return if (result.hasError()) Result.failure() else Result.success()
    }

    companion object {
        const val UNIQUE_NAME = "ReadsWorker"
    }
}
