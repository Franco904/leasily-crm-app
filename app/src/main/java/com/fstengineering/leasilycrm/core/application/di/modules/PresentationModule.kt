package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.presentation.external.CredentialManagerAdapterImpl
import com.fstengineering.leasilycrm.core.presentation.external.interfaces.CredentialManagerAdapter
import org.koin.dsl.module

val presentationModule = module {
    single<CredentialManagerAdapter> { CredentialManagerAdapterImpl() }
}
