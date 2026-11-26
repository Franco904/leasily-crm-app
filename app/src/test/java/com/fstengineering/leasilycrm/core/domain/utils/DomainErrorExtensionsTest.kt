package com.fstengineering.leasilycrm.core.domain.utils

import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DomainErrorExtensionsTest {
    @Nested
    @DisplayName("toDomainErrors")
    inner class ToDomainErrorsTests {
        @Test
        fun `Deve devolver o próprio erro quando é um erro de domínio`() {
            val sut: BaseError = DomainError.Name.Required

            sut.toDomainErrors() shouldBeEqualTo listOf(DomainError.Name.Required)
        }

        @Test
        fun `Deve devolver a lista de violações quando são erros de validação acumulados`() {
            val violations = persistentListOf<DomainError>(
                DomainError.Name.Required,
                DomainError.Email.Invalid,
            )
            val sut: BaseError = ValidationErrors(violations)

            sut.toDomainErrors() shouldBeEqualTo violations
        }

        @Test
        fun `Deve falhar quando o erro não é de domínio, pois a factory nunca o devolve`() {
            val sut: BaseError = ApplicationError.Unknown

            invoking { sut.toDomainErrors() } shouldThrow IllegalStateException::class
        }
    }
}
