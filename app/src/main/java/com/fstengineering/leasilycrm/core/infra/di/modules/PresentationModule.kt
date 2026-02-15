package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.CredentialManagerAdapterImpl
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import org.koin.dsl.module

val presentationModule = module {
    single<CredentialManagerAdapter> { CredentialManagerAdapterImpl() }
}
