package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.domain.validators.AssistanceInteractionValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.AssistanceSearchedNeighborhoodValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.AssistanceValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.PropertyOwnerValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.PropertyPriceValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.PropertyValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.StayTransferValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.StayValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.TenantValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.UserValidatorImpl
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceInteractionValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceSearchedNeighborhoodValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyPriceValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayTransferValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.TenantValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.UserValidator
import org.koin.dsl.module

val validatorModule = module {
    single<AssistanceValidator> { AssistanceValidatorImpl() }
    single<AssistanceInteractionValidator> { AssistanceInteractionValidatorImpl() }
    single<AssistanceSearchedNeighborhoodValidator> { AssistanceSearchedNeighborhoodValidatorImpl() }
    single<PropertyValidator> { PropertyValidatorImpl() }
    single<PropertyPriceValidator> { PropertyPriceValidatorImpl() }
    single<PropertyOwnerValidator> { PropertyOwnerValidatorImpl() }
    single<TenantValidator> { TenantValidatorImpl() }
    single<StayValidator> { StayValidatorImpl() }
    single<StayTransferValidator> { StayTransferValidatorImpl() }
    single<UserValidator> { UserValidatorImpl() }
}
