package com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user.SignInUserResponseDto

interface UserRemoteDataSource {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, ApplicationError>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, ApplicationError>

    suspend fun signOutUser(): AppResult<Unit, ApplicationError>
}
