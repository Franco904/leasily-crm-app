package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.SignInDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user.GetUserDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface UserApi {
    suspend fun signInUser(
        signInDto: SignInDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError>

    suspend fun getUser(): AppResult<GetUserDto, DataSourceError.RemoteBackendError>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError>

    suspend fun signOutUser(): AppResult<Unit, DataSourceError.RemoteBackendError>
}
