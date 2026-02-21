package com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface UserRemoteDataSource {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, InfraError>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, InfraError>

    suspend fun signOutUser(): AppResult<Unit, InfraError>
}
