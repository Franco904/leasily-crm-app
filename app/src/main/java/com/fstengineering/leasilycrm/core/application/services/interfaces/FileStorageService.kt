package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface FileStorageService {
    suspend fun getFileName(contentUriString: String): AppResult<String?, ApplicationError>
}
