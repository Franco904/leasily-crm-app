package com.fstengineering.leasilycrm.propertyRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects.PropertyPricePeriod
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class PropertyPriceTest {
    private val d1 = AppDate(LocalDate.of(2025, 1, 1))
    private val d5 = AppDate(LocalDate.of(2025, 1, 5))
    private val d10 = AppDate(LocalDate.of(2025, 1, 10))
    private val d15 = AppDate(LocalDate.of(2025, 1, 15))
    private val validAmount = AppDecimal(BigDecimal("1500.00"))

    private fun period(start: AppDate, end: AppDate): PropertyPricePeriod =
        (PropertyPricePeriod.parse(startDate = start, endDate = end) as AppResultCompound.Success).data

    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve criar preço de imóvel com sucesso quando todos os dados são válidos`() {
            val result = PropertyPrice.parse(
                startDate = d1,
                endDate = d10,
                amount = validAmount,
            )

            result.shouldBeInstanceOf<AppResultCompound.Success<*, *>>()
        }

        @Test
        fun `Deve retornar erro quando data de início está ausente`() {
            val result = PropertyPrice.parse(
                startDate = null,
                endDate = d10,
                amount = validAmount,
            )

            val errors = result.shouldBeInstanceOf<AppResultCompound.Errors<*, *>>()
            errors.errors shouldContain DomainError.PropertyPricePeriod.Required
        }

        @Test
        fun `Deve retornar erro quando data de fim está ausente`() {
            val result = PropertyPrice.parse(
                startDate = d1,
                endDate = null,
                amount = validAmount,
            )

            val errors = result.shouldBeInstanceOf<AppResultCompound.Errors<*, *>>()
            errors.errors shouldContain DomainError.PropertyPricePeriod.Required
        }

        @Test
        fun `Deve retornar erro quando valor do preço está ausente`() {
            val result = PropertyPrice.parse(
                startDate = d1,
                endDate = d10,
                amount = null,
            )

            val errors = result.shouldBeInstanceOf<AppResultCompound.Errors<*, *>>()
            errors.errors shouldContain DomainError.Amount.Required
        }

        @Test
        fun `Deve propagar erro de período inválido quando datas são iguais`() {
            val result = PropertyPrice.parse(
                startDate = d10,
                endDate = d10,
                amount = validAmount,
            )

            val errors = result.shouldBeInstanceOf<AppResultCompound.Errors<*, *>>()
            errors.errors shouldContain DomainError.PropertyPricePeriod.StartAndEndDatesAreTheSame
        }

        @Test
        fun `Deve propagar erro de sobreposição quando novo período conflita com existente`() {
            val existing = period(start = d1, end = d10)

            val result = PropertyPrice.parse(
                startDate = d5,
                endDate = d15,
                amount = validAmount,
                otherPeriods = listOf(existing),
            )

            val errors = result.shouldBeInstanceOf<AppResultCompound.Errors<*, *>>()
            errors.errors shouldContain DomainError.PropertyPricePeriod.IsOverlappingOtherPricePeriod
        }
    }
}
