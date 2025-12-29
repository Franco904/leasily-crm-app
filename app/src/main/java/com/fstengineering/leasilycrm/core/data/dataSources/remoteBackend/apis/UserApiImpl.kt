package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.HttpClientAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user.GetUserDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class UserApiImpl(
    private val client: HttpClientAdapter,
) : UserApi {
    override suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, DataError> {
        return client.request(
            url = "$ROUTE/sign-in",
            method = HttpMethod.Post,
            body = signInRequestDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { response -> AppResult.Success(data = response.body()) },
            )
    }

    override suspend fun getUser(): AppResult<GetUserDto, DataError> {
        return client.request(
            url = ROUTE,
            method = HttpMethod.Get,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { response -> AppResult.Success(data = response.body()) },
            )
    }

    override suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, DataError> {
        return client.request(
            url = "$ROUTE/update",
            method = HttpMethod.Put,
            body = updateUserDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun signOutUser(): AppResult<Unit, DataError> {
        return client.request(
            url = "$ROUTE/sign-out",
            method = HttpMethod.Get,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    companion object {
        private const val ROUTE = "/users"
    }
}
