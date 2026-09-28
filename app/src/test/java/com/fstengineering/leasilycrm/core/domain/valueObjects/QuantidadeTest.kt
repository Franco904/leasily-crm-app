package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QuantidadeTest {
    @Nested
    @DisplayName("of")
    inner class OfTests {
        @Test
        fun `Deve manter a quantidade informada quando ela não é negativa`() {
            val count = faker.random.nextInt(bound = 9999)

            val result = Quantidade.of(count)

            result.value shouldBeEqualTo count
        }

        @Test
        fun `Deve considerar quantidade zero quando a quantidade informada é negativa`() {
            val count = -(faker.random.nextInt(bound = 9999) + 1)

            val result = Quantidade.of(count)

            result.value shouldBeEqualTo 0
        }
    }
}
