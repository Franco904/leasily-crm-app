package com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class InteracaoTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar Interação com todos os erros de uma vez quando registrada, dado que data está ausente e título está em branco`() {
            val result = Interacao.parse(id = null, date = null, title = "  ", description = null)

            result.shouldBeInstanceOf<AppResult.Error>().error
                .shouldBeInstanceOf<ValidationErrors>().errors shouldContainSame listOf(
                DomainError.InteracaoDate.Required,
                DomainError.InteracaoTitle.Required,
            )
        }

        @Test
        fun `Deve aceitar Interação sem descrição quando registrada, dado que data e título foram informados`() {
            val result = Interacao.parse(
                id = null,
                date = AppDateProviderImpl().now(),
                title = "Ligação",
                description = null,
            )

            result.shouldBeInstanceOf<AppResult.Success<Interacao>>()
                .data.title shouldBeEqualTo "Ligação"
        }
    }
}
