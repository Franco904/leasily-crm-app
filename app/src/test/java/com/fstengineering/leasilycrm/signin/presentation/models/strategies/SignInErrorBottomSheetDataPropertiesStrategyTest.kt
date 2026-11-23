package com.fstengineering.leasilycrm.signin.presentation.models.strategies

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SignInErrorBottomSheetDataPropertiesStrategyTest {
    @Nested
    @DisplayName("BaseError.toBottomSheetData")
    inner class ToBottomSheetDataTests {
        @Test
        fun `Deve usar a descrição de entrar quando falta internet na conta Google`() {
            val sut: BaseError = ApplicationError.NoInternet

            val result = sut.toBottomSheetData(SignInErrorBottomSheetDataPropertiesStrategy)

            result.title shouldBeEqualTo SignInErrorBottomSheetDataPropertiesStrategy.noInternetErrorTitle
            result.description shouldBeEqualTo R.string.no_internet_error_description_sign_in
        }
    }
}
