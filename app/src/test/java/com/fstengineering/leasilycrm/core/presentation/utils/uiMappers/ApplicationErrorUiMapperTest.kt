package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy as Strategy
import com.fstengineering.leasilycrm.signin.presentation.models.strategies.SignInErrorBottomSheetDataPropertiesStrategy as SignInStrategy

class ApplicationErrorUiMapperTest {
    @Nested
    @DisplayName("BaseError.toBottomSheetData")
    inner class ToBottomSheetDataTests {
        @Test
        fun `Deve usar os textos da strategy quando é um erro de infra`() {
            val sut: BaseError = ApplicationError.NoInternet

            val result = sut.toBottomSheetData(Strategy)

            result.title shouldBeEqualTo Strategy.noInternetErrorTitle
            result.description shouldBeEqualTo Strategy.noInternetErrorDescription
        }

        @Test
        fun `Deve usar os textos de credenciais quando não há credenciais`() {
            val sut: BaseError = ApplicationError.NoCredentialsFound

            val result = sut.toBottomSheetData(Strategy)

            result.title shouldBeEqualTo R.string.no_credentials_found_error_title
        }

        @Test
        fun `Deve usar a descrição de entrar quando falta internet na conta Google`() {
            val sut: BaseError = ApplicationError.NoInternet

            val result = sut.toBottomSheetData(SignInStrategy)

            result.title shouldBeEqualTo SignInStrategy.noInternetErrorTitle
            result.description shouldBeEqualTo R.string.no_internet_error_description_sign_in
        }

        @Test
        fun `Deve usar os textos de erro desconhecido quando é um erro de domínio`() {
            val sut: BaseError = DomainError.Name.Required

            val result = sut.toBottomSheetData(Strategy)

            result.title shouldBeEqualTo Strategy.unknownErrorTitle
        }

        @Test
        fun `Deve usar os textos de erro desconhecido quando é um erro de validação`() {
            val sut: BaseError = ValidationErrors(persistentListOf(DomainError.Name.Required))

            val result = sut.toBottomSheetData(Strategy)

            result.title shouldBeEqualTo Strategy.unknownErrorTitle
        }
    }
}
