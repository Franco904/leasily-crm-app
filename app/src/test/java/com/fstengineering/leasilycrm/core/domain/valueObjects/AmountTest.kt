package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AmountTest {
    @Nested
    @DisplayName("of")
    inner class OfTests {
        @Test
        fun `Deve manter o valor informado quando ele não é negativo`() {
            val value = AppDecimal(BigDecimal.valueOf(faker.random.nextLong(bound = 999999), 2))

            val result = Amount.of(value)

            result.value shouldBeEqualTo value
        }

        @Test
        fun `Deve descartar o sinal quando o valor informado é negativo`() {
            val absoluteValue = BigDecimal.valueOf(faker.random.nextLong(bound = 999999) + 1, 2)

            val result = Amount.of(AppDecimal(absoluteValue.negate()))

            result.value shouldBeEqualTo AppDecimal(absoluteValue)
        }
    }
}
