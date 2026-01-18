package com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remote.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface UserRemoteDataSource {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, DataError>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, DataError>

    suspend fun signOutUser(): AppResult<Unit, DataError>
}
