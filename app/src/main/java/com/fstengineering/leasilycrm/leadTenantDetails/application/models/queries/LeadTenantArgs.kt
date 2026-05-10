package com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase

data class LeadTenantArgs(val tenantId: Long) : QueryUseCase.Args
