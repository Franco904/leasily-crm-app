package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.StayFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface StayFileRepository {
    suspend fun getFileName(file: StayFile): AppResult<String, ApplicationError>
}
