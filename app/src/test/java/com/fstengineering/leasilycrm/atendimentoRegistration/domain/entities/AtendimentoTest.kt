package com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AtendimentoTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar Atendimento com todos os erros de uma vez quando cadastrado, dado que o Cliente está ausente, o período projetado tem datas iguais e há bairro em branco`() {
            val date = AppDate(LocalDate.of(2030, 1, 10))

            val result = parse(
                clienteId = null,
                preferredStartDate = date,
                preferredEndDate = date,
                preferredNeighborhoods = listOf(" "),
            )

            result.shouldBeInstanceOf<AppResult.Error>().error
                .shouldBeInstanceOf<ValidationErrors>().errors shouldContainSame listOf(
                DomainError.AtendimentoOtherFields.TenantRequired,
                DomainError.AtendimentoOtherFields.PreferredPeriodStartAndEndDatesAreTheSame,
                DomainError.Neighborhood.Required,
            )
        }

        @Test
        fun `Deve rejeitar Atendimento quando cadastrado, dado que o período projetado termina antes de começar`() {
            val result = parse(
                preferredStartDate = AppDate(LocalDate.of(2030, 1, 10)),
                preferredEndDate = AppDate(LocalDate.of(2030, 1, 5)),
            )

            result.shouldBeInstanceOf<AppResult.Error>().error
                .shouldBeInstanceOf<ValidationErrors>().errors shouldBeEqualTo listOf(
                DomainError.AtendimentoOtherFields.PreferredPeriodStartDateIsAfterEndDate,
            )
        }

        @Test
        fun `Deve aceitar Atendimento sem período projetado quando cadastrado, dado que só o Cliente foi informado`() {
            val result = parse(clienteId = 4L)

            val atendimento = result.shouldBeInstanceOf<AppResult.Success<Atendimento>>().data
            atendimento.clienteId shouldBeEqualTo 4L
            atendimento.preferredPeriod shouldBeEqualTo null
        }
    }

    private fun parse(
        clienteId: Long? = 1L,
        preferredStartDate: AppDate? = null,
        preferredEndDate: AppDate? = null,
        preferredNeighborhoods: List<String> = emptyList(),
    ) = Atendimento.parse(
        id = null,
        clienteId = clienteId,
        createDateTime = null,
        preferredStartDate = preferredStartDate,
        preferredEndDate = preferredEndDate,
        hasFlexibilityForPeriod = false,
        maxAcceptedNightlyRate = null,
        adultsCount = 1,
        kidsCount = 0,
        babiesCount = 0,
        animalsCount = 0,
        bedroomsCount = 0,
        enSuitesCount = 0,
        doubleBedsCount = 0,
        singleBedsCount = 0,
        bathroomsCount = 0,
        parkingSpotsCount = 0,
        preferredSeaProximityTypeId = null,
        preferredDistanceToSea = null,
        preferredNeighborhoods = persistentListOf<String>().addAll(preferredNeighborhoods),
        preferredImovelCharacteristics = persistentListOf(),
        observations = null,
        imoveisEnviadosIds = persistentListOf(),
        interacoes = persistentListOf(),
        isFinished = false,
    )
}
