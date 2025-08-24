package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.domain.validators.CustomerSegmentValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.UserValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.CustomerSegmentValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.UserValidator
import org.koin.dsl.module

val validatorModule = module {
    single<CustomerSegmentValidator> { CustomerSegmentValidatorImpl() }
    single<UserValidator> { UserValidatorImpl() }
}
