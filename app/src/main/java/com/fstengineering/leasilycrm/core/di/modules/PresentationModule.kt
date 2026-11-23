package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.CredentialManagerAdapterImpl
import com.fstengineering.leasilycrm.core.infra.adapters.CurrentActivityTracker
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import org.koin.dsl.module

val presentationModule = module {
    single { CurrentActivityTracker() }
    single<CredentialManagerAdapter> {
        CredentialManagerAdapterImpl(connectivityAdapter = get(), currentActivityTracker = get())
    }
}
