package com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProprietarioFileTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar arquivo com todos os erros de uma vez quando registrado, dado que endereço e nome estão em branco`() {
            val result = ProprietarioFile.parse(uriString = " ", name = "")

            result.shouldBeInstanceOf<AppResult.Error>().error
                .shouldBeInstanceOf<ValidationErrors>().errors shouldContainSame listOf(
                DomainError.ProprietarioFileUri.Required,
                DomainError.ProprietarioFileName.Required,
            )
        }

        @Test
        fun `Deve aceitar arquivo quando registrado, dado que endereço e nome foram informados`() {
            val result = ProprietarioFile.parse(uriString = "content://documento", name = "contrato.pdf")

            result.shouldBeInstanceOf<AppResult.Success<ProprietarioFile>>()
                .data.name shouldBeEqualTo "contrato.pdf"
        }
    }
}
