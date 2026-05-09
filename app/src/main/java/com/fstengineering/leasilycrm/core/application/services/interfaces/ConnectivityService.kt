package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface ConnectivityService {
    fun checkDeviceIsConnectedToInternet(): AppResult<Unit, ApplicationError>
}
