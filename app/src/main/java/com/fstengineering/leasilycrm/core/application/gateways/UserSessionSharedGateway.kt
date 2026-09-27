package com.fstengineering.leasilycrm.core.application.gateways

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface UserSessionSharedGateway {
    suspend fun killSession(): AppResult<Unit, ApplicationError>
}
