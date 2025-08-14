package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.UpdateUserRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.GetUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface UserApi {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError>

    suspend fun getUser(): AppResult<GetUserResponseDto, DataSourceError.RemoteBackendError>

    suspend fun updateUser(
        updateUserRequestDto: UpdateUserRequestDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError>

    suspend fun signOutUser(): AppResult<Unit, DataSourceError.RemoteBackendError>
}
