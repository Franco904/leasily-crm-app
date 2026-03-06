package com.fstengineering.leasilycrm.core.application.services

import com.fstengineering.leasilycrm.core.application.services.interfaces.ConnectivityService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter

class ConnectivityServiceImpl(
    private val connectivityAdapter: ConnectivityAdapter,
) : ConnectivityService {
    override fun checkDeviceIsConnectedToInternet(): AppResult<Unit, ApplicationError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = ApplicationError.Infra.NoInternet)
        }

        return AppResult.Success(data = Unit)
    }
}
