package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface FileStorageService {
    suspend fun getFileName(contentUriString: String): AppResult<String?, ApplicationError>
}
