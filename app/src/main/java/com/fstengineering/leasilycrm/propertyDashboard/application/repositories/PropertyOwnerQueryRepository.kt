package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface PropertyOwnerQueryRepository {
    suspend fun getNameById(propertyOwnerId: Long): AppResult<String, ApplicationError>
}
