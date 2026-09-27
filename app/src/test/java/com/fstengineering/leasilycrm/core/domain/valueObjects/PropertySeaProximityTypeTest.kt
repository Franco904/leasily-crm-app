package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PropertySeaProximityTypeTest {
    @Nested
    @DisplayName("DistanceToSea.parse")
    inner class DistanceToSeaParseTests {
        @Test
        fun `Deve manter a distância informada quando ela não é negativa`() {
            val distance = AppDecimal(BigDecimal.valueOf(faker.random.nextLong(bound = 999999), 2))

            val result = PropertySeaProximityType.DistanceToSea.parse(distance)

            val success = result.shouldBeInstanceOf<AppResult.Success<PropertySeaProximityType.DistanceToSea, ValidationErrors>>()
            success.data.distance shouldBeEqualTo distance
        }

        @Test
        fun `Deve descartar o sinal quando a distância informada é negativa`() {
            val absoluteDistance = BigDecimal.valueOf(faker.random.nextLong(bound = 999999) + 1, 2)

            val result = PropertySeaProximityType.DistanceToSea.parse(AppDecimal(absoluteDistance.negate()))

            val success = result.shouldBeInstanceOf<AppResult.Success<PropertySeaProximityType.DistanceToSea, ValidationErrors>>()
            success.data.distance shouldBeEqualTo AppDecimal(absoluteDistance)
        }

        @Test
        fun `Deve falhar com distância obrigatória quando a distância não é informada`() {
            val result = PropertySeaProximityType.DistanceToSea.parse(null)

            result shouldBeEqualTo AppResult.Error(
                ValidationErrors(persistentListOf(DomainError.PropertySeaProximityDistanceToSea.Required))
            )
        }
    }
}
