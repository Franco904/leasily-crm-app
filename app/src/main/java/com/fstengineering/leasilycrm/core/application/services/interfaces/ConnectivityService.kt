package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface ConnectivityService {
    fun checkDeviceIsConnectedToInternet(): AppResult<Unit, ApplicationError>
}
