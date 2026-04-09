package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import kotlinx.collections.immutable.ImmutableList

data class PropertyOwnerCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFileCommandModel>,
) : CommandUseCase.CommandModel
