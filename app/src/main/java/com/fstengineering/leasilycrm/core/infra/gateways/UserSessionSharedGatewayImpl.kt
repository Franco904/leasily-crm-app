package com.fstengineering.leasilycrm.core.infra.gateways

import com.fstengineering.leasilycrm.core.application.gateways.UserSessionSharedGateway
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource

class UserSessionSharedGatewayImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: UserRemoteDataSource,
) : UserSessionSharedGateway {
    override suspend fun killSession(): AppResult<Unit, ApplicationError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = ApplicationError.NoInternet)
        }

        remoteDataSource.signOutUser()
            .onError { e ->
                if (e != ApplicationError.Unauthorized) return AppResult.Error(error = e)
            }

        return AppResult.Success(data = Unit)
    }
}
