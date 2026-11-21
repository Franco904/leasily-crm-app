package com.fstengineering.leasilycrm.clienteRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClienteTest {
    @Nested
    @DisplayName("parse")
    inner class ParseTests {
        @Test
        fun `Deve rejeitar Cliente com todos os erros de campo de uma vez quando cadastrado, dado que nome está ausente, e-mail é inválido e telefone excede 20 caracteres`() {
            val result = parse(
                name = null,
                phone = randomString(minLength = 21),
                email = faker.random.nextUUID(),
            )

            val error = result.shouldBeInstanceOf<AppResult.Error>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContainSame listOf(
                DomainError.Name.Required,
                DomainError.Email.Invalid,
                DomainError.Phone.LengthIsGreaterThan20Chars,
            )
        }

        @Test
        fun `Deve rejeitar Cliente quando cadastrado, dado que nome excede 100 caracteres`() {
            val result = parse(name = randomString(minLength = 101))

            val error = result.shouldBeInstanceOf<AppResult.Error>().error
            val validationErrors = error.shouldBeInstanceOf<ValidationErrors>()
            validationErrors.errors shouldContainSame listOf(
                DomainError.Name.LengthIsGreaterThan100Chars,
            )
        }

        @Test
        fun `Deve criar Cliente quando cadastrado, dado que todos os dados são válidos`() {
            val id = faker.random.nextInt(bound = 9999).toLong()
            val name = faker.random.nextUUID()
            val phone = faker.random.nextInt(bound = 999999999).toString()
            val email = "${faker.random.nextUUID()}@leasily.com"

            val result = parse(id = id, name = name, phone = phone, email = email)

            val cliente = result.shouldBeInstanceOf<AppResult.Success<*>>()
                .data.shouldBeInstanceOf<Cliente>()
            cliente.id shouldBeEqualTo id
            cliente.name.value shouldBeEqualTo name
            cliente.phone?.value shouldBeEqualTo phone
            cliente.email?.value shouldBeEqualTo email
        }
    }

    private fun parse(
        id: Long? = null,
        name: String? = faker.random.nextUUID(),
        phone: String? = null,
        email: String? = null,
    ) = Cliente.parse(
        id = id,
        createDateTime = null,
        tag = null,
        name = name,
        city = null,
        identityDocument = null,
        phone = phone,
        email = email,
        contactOrigin = null,
        observations = null,
    )

    private fun randomString(minLength: Int): String =
        "x".repeat(minLength + faker.random.nextInt(bound = 50))
}
