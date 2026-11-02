package com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProprietarioTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar Proprietário com todos os erros de campo de uma vez quando cadastrado, dado que nome está ausente, e-mail é inválido, telefone excede 20 caracteres e chave de pagamento excede 120 caracteres`() {
            val result = Proprietario.parse(
                id = null,
                createDateTime = null,
                name = null,
                phone = randomString(minLength = 21),
                email = faker.random.nextUUID(),
                paymentKey = randomString(minLength = 121),
                files = persistentListOf(),
            )

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContainSame listOf(
                DomainError.Name.Required,
                DomainError.Email.Invalid,
                DomainError.Phone.LengthIsGreaterThan20Chars,
                DomainError.PaymentKey.LengthIsGreaterThan120Chars,
            )
        }

        @Test
        fun `Deve rejeitar Proprietário quando cadastrado, dado que nome excede 100 caracteres`() {
            val result = Proprietario.parse(
                id = null,
                createDateTime = null,
                name = randomString(minLength = 101),
                phone = null,
                email = null,
                paymentKey = null,
                files = persistentListOf(),
            )

            val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContainSame listOf(
                DomainError.Name.LengthIsGreaterThan100Chars,
            )
        }

        @Test
        fun `Deve criar Proprietário quando cadastrado, dado que todos os dados são válidos`() {
            val id = faker.random.nextInt(bound = 9999).toLong()
            val name = faker.random.nextUUID()
            val phone = faker.random.nextInt(bound = 999999999).toString()
            val email = "${faker.random.nextUUID()}@leasily.com"
            val paymentKey = faker.random.nextUUID()

            val result = Proprietario.parse(
                id = id,
                createDateTime = null,
                name = name,
                phone = phone,
                email = email,
                paymentKey = paymentKey,
                files = persistentListOf(),
            )

            val proprietario = result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldBeInstanceOf<Proprietario>()
            proprietario.id shouldBeEqualTo id
            proprietario.name.value shouldBeEqualTo name
            proprietario.phone?.value.shouldBeEqualTo(phone)
            proprietario.email?.value.shouldBeEqualTo(email)
            proprietario.paymentKey?.value.shouldBeEqualTo(paymentKey)
        }
    }

    private fun randomString(minLength: Int): String =
        "x".repeat(minLength + faker.random.nextInt(bound = 50))
}
