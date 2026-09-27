package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WritePropertyOwnerUseCaseTest {
    private val propertyOwnerCommandRepositoryMock: PropertyOwnerCommandRepository =
        mockk(relaxed = true)

    private val sut = WritePropertyOwnerUseCase(
        propertyOwnerCommandRepository = propertyOwnerCommandRepositoryMock,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("invoke")
    inner class InvokeTests {
        @Test
        fun `Deve rejeitar Proprietário sem persisti-lo quando cadastrado, dado que o nome está ausente`() =
            runTest {
                val command = createValidCommand().copy(name = null)

                val result = sut(command)

                val error = result.shouldBeInstanceOf<AppResult.Error<*, *>>().error
                error.shouldBeInstanceOf<ValidationErrors>().errors shouldContainSame listOf(
                    DomainError.Name.Required,
                )
                coVerify(exactly = 0) { propertyOwnerCommandRepositoryMock.insertOrUpdate(any()) }
            }

        @Test
        fun `Deve informar falha de persistência quando cadastrado, dado que o armazenamento local falha`() =
            runTest {
                val command = createValidCommand()
                coEvery { propertyOwnerCommandRepositoryMock.insertOrUpdate(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                val result = sut(command)

                result.shouldBeInstanceOf<AppResult.Error<*, *>>().error shouldBeEqualTo
                    ApplicationError.Unknown
            }

        @Test
        fun `Deve persistir Proprietário quando cadastrado, dado que os dados são válidos`() =
            runTest {
                val command = createValidCommand()
                coEvery { propertyOwnerCommandRepositoryMock.insertOrUpdate(any()) } returns
                    AppResult.Success(Unit)

                val result = sut(command)

                result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                coVerify {
                    propertyOwnerCommandRepositoryMock.insertOrUpdate(
                        match { it.name.value == command.name && it.email?.value == command.email },
                    )
                }
            }
    }

    private fun createValidCommand() = PropertyOwnerCommandModel(
        id = faker.random.nextInt(bound = 9999).toLong(),
        createDateTime = null,
        name = faker.random.nextUUID(),
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf(),
    )
}
