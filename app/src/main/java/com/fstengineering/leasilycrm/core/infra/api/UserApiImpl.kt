package com.fstengineering.leasilycrm.core.infra.api

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_IN_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_OUT_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.UPDATE_USER_ROUTE
import com.fstengineering.leasilycrm.core.infra.api.interfaces.UserApi
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.api.responseDtos.user.SignInUserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class UserApiImpl(
    private val client: HttpClient,
) : UserApi {
    override suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto> {
        return client.requestBackend(
            url = SIGN_IN_ROUTE,
            method = HttpMethod.Post,
            body = signInRequestDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { response -> AppResult.Success(data = response.body()) },
            )
    }

    override suspend fun signOutUser(): AppResult<Unit> {
        return client.requestBackend(
            url = SIGN_OUT_ROUTE,
            method = HttpMethod.Get,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit> {
        return client.requestBackend(
            url = UPDATE_USER_ROUTE,
            method = HttpMethod.Put,
            body = updateUserDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }
}
