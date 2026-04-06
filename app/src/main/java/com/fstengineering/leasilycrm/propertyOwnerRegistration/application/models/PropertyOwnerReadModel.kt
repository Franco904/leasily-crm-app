package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.ImmutableList

data class PropertyOwnerReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFileReadModel>,
) : QueryUseCase.ReadModel
