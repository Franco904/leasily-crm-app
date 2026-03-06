package com.fstengineering.leasilycrm.signin.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.signin.application.repositories.SignInRepository
import com.fstengineering.leasilycrm.signin.domain.entities.User
import com.fstengineering.leasilycrm.signin.infra.utils.toEntity

class SignInRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
) : SignInRepository {
    override suspend fun signIn(idToken: String): AppResult<User, ApplicationError> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = remoteDataSource.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { responseDto -> responseDto }
            )

        val user = signInResponseDto.toEntity()

        return AppResult.Success(data = user)
    }
}
