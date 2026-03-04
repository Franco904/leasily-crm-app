package com.fstengineering.leasilycrm.assistanceRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.entities.Property
import kotlinx.collections.immutable.ImmutableList

data class AssistanceReadModel(
    val assistance: Assistance?,
    val sentProperties: ImmutableList<Property>,
    val leadTenantId: Long,
    val leadTenantName: String,
) : QueryUseCase.ReadModel
