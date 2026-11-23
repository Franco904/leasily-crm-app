package com.fstengineering.leasilycrm.imovelRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ImovelFileTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar foto quando registrada, dado que o endereço está em branco`() {
            val result = ImovelFile.parse(uriString = " ", isMainPicture = false)

            result.shouldBeInstanceOf<AppResult.Error>().error
                .shouldBeInstanceOf<ValidationErrors>().errors shouldContainSame listOf(
                DomainError.ImovelFileUri.Required,
            )
        }

        @Test
        fun `Deve aceitar foto principal quando registrada, dado que o endereço foi informado`() {
            val result = ImovelFile.parse(uriString = "content://foto", isMainPicture = true)

            result.shouldBeInstanceOf<AppResult.Success<ImovelFile>>()
                .data.isMainPicture shouldBeEqualTo true
        }
    }
}
