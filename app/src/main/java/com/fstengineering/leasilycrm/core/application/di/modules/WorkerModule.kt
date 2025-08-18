package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.LazyWritesWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.ReadsWorker
import com.fstengineering.leasilycrm.core.application.backgroundWork.workers.SessionExpirationWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker<LazyWritesWorker> {
        LazyWritesWorker(
            applicationContext = get(),
            workerParameters = get(),
            userRepository = get(),
        )
    }

    worker<ReadsWorker> {
        ReadsWorker(
            applicationContext = get(),
            workerParameters = get(),
            userRepository = get(),
        )
    }

    worker<SessionExpirationWorker> {
        SessionExpirationWorker(
            applicationContext = get(),
            workerParameters = get(),
            userRepository = get(),
        )
    }
}
