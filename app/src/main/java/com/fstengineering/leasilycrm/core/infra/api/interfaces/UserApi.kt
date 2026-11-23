package com.fstengineering.leasilycrm.core.infra.api.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.api.responseDtos.user.SignInUserResponseDto

interface UserApi {
    suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto>

    suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit>

    suspend fun signOutUser(): AppResult<Unit>
}
