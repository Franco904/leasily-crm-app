package com.fstengineering.leasilycrm.core.application.gateways

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface UserSessionSharedGateway {
    suspend fun killSession(): AppResult<Unit, ApplicationError>
}
