package com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelPricesUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class UiStateToImovelTest {
    @Nested
    @DisplayName("toImovel")
    inner class ToImovelTests {
        @Test
        fun `Deve reunir os Preços de todos os meses no Imóvel quando os dados são válidos`() {
            val januaryPrice = createPriceUiState(
                id = 1L,
                startDay = LocalDate.of(2025, 1, 1),
                endDay = LocalDate.of(2025, 1, 10)
            )
            val februaryPrice = createPriceUiState(
                id = 2L,
                startDay = LocalDate.of(2025, 2, 1),
                endDay = LocalDate.of(2025, 2, 10)
            )
            val form = createValidForm().copy(
                prices = ImovelPricesUiState(
                    persistentMapOf(
                        PriceMonthYearUiState(month = 1, year = 2025) to persistentListOf(
                            januaryPrice
                        ),
                        PriceMonthYearUiState(month = 2, year = 2025) to persistentListOf(
                            februaryPrice
                        ),
                    ),
                ),
            )

            val result = form.toImovel()

            val imovel =
                result.shouldBeInstanceOf<AppResult.Success<Imovel, ValidationErrors>>().data
            imovel.prices.map { it.id }.toSet() shouldBeEqualTo setOf(1L, 2L)
        }

        @Test
        fun `Deve acumular erros do Preço e do Imóvel quando ambos são inválidos`() {
            val invalidPrice = createPriceUiState(
                id = 1L,
                startDay = LocalDate.of(2025, 1, 10),
                endDay = LocalDate.of(2025, 1, 1),
            )
            val form = createValidForm().copy(
                neighborhood = null,
                prices = ImovelPricesUiState(
                    persistentMapOf(
                        PriceMonthYearUiState(month = 1, year = 2025) to persistentListOf(
                            invalidPrice
                        ),
                    ),
                ),
            )

            val result = form.toImovel()

            val errors =
                result.shouldBeInstanceOf<AppResult.Error<Imovel, ValidationErrors>>().error.errors
            errors shouldBeEqualTo listOf(
                DomainError.Neighborhood.Required,
                DomainError.ImovelPricePeriod.StartDateIsAfterEndDate,
            )
        }
    }

    private fun createValidForm() = ImovelDraftFormUiState.Loaded(
        proprietario = ProprietarioUiState(
            id = faker.random.nextLong(bound = 9999),
            name = faker.random.nextUUID(),
        ),
        type = ImovelType.Apartment,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
    )

    private fun createPriceUiState(id: Long, startDay: LocalDate, endDay: LocalDate) =
        ImovelPriceUiState(
            id = id,
            startDate = AppDate(startDay),
            endDate = AppDate(endDay),
            amount = AppDecimal(BigDecimal(faker.random.nextInt(bound = 9999) + 1)),
        )
}
