package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface ProprietarioQueryRepository {
    suspend fun getNameById(proprietarioId: Long): AppResult<String, ApplicationError>
}
