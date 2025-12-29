package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user.GetUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface UserApi {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, DataError>

    suspend fun getUser(): AppResult<GetUserDto, DataError>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, DataError>

    suspend fun signOutUser(): AppResult<Unit, DataError>
}
