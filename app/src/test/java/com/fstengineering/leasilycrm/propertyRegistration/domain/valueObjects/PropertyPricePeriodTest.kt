package com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PropertyPricePeriodTest {
    private val d1 = AppDate(LocalDate.of(2025, 1, 1))
    private val d5 = AppDate(LocalDate.of(2025, 1, 5))
    private val d10 = AppDate(LocalDate.of(2025, 1, 10))
    private val d15 = AppDate(LocalDate.of(2025, 1, 15))
    private val d20 = AppDate(LocalDate.of(2025, 1, 20))

    private fun period(start: AppDate, end: AppDate): PropertyPricePeriod =
        (PropertyPricePeriod.parse(
            startDate = start,
            endDate = end,
        ) as AppResult.Success).data

    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve criar período com sucesso quando não há outros períodos`() {
            val result = PropertyPricePeriod.parse(startDate = d1, endDate = d10)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
        }

        @Test
        fun `Deve retornar erro quando data de início e data de fim são iguais`() {
            val result = PropertyPricePeriod.parse(startDate = d10, endDate = d10)

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContain DomainError.PropertyPricePeriod.StartAndEndDatesAreTheSame
        }

        @Test
        fun `Deve retornar erro quando data de início é posterior à data de fim`() {
            val result = PropertyPricePeriod.parse(startDate = d15, endDate = d5)

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContain DomainError.PropertyPricePeriod.StartDateIsAfterEndDate
        }

        @Test
        fun `Deve retornar erro de sobreposição quando novo período está contido em outro existente`() {
            val existing = period(start = d1, end = d20)

            val result = PropertyPricePeriod.parse(
                startDate = d5,
                endDate = d15,
                otherPeriods = listOf(existing),
            )

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContain DomainError.PropertyPricePeriod.IsOverlappingOtherPricePeriod
        }

        @Test
        fun `Deve retornar erro de sobreposição quando períodos se sobrepõem parcialmente`() {
            val existing = period(start = d1, end = d10)

            val result = PropertyPricePeriod.parse(
                startDate = d5,
                endDate = d15,
                otherPeriods = listOf(existing),
            )

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContain DomainError.PropertyPricePeriod.IsOverlappingOtherPricePeriod
        }

        @Test
        fun `Deve criar período com sucesso quando períodos são adjacentes com endpoint compartilhado`() {
            val existing = period(start = d1, end = d10)

            val result = PropertyPricePeriod.parse(
                startDate = d10,
                endDate = d20,
                otherPeriods = listOf(existing),
            )

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
        }

        @Test
        fun `Deve criar período com sucesso ao editar quando período original é excluído dos outros períodos`() {
            val adjacentBefore = period(start = d1, end = d5)
            val adjacentAfter = period(start = d15, end = d20)

            val result = PropertyPricePeriod.parse(
                startDate = d5,
                endDate = d15,
                otherPeriods = listOf(adjacentBefore, adjacentAfter),
            )

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
        }
    }
}
