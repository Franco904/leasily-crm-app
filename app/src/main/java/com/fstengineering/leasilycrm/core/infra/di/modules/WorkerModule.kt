package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.backgroundWork.workers.BootstrappingWorker
import com.fstengineering.leasilycrm.core.infra.backgroundWork.workers.LazyWritesWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker<BootstrappingWorker> {
        BootstrappingWorker(
            applicationContext = get(),
            workerParameters = get(),
        )
    }

    worker<LazyWritesWorker> {
        LazyWritesWorker(
            applicationContext = get(),
            workerParameters = get(),
            userSyncStore = get(),
        )
    }
}
