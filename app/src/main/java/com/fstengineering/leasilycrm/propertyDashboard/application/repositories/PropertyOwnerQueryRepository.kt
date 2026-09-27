package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface PropertyOwnerQueryRepository {
    suspend fun getNameById(propertyOwnerId: Long): AppResult<String, ApplicationError>
}
