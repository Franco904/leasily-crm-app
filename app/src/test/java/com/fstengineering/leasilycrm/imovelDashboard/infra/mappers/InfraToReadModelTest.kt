package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ProprietarioProjection
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InfraToReadModelTest {
    @BeforeEach
    fun setUp() {
        mockkObject(ExceptionLogger)
        every { ExceptionLogger.logMasked(any()) } just runs
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(ExceptionLogger)
    }

    @Nested
    @DisplayName("ImovelDashboardProjection.toReadModel")
    inner class ToReadModelTests {
        @Test
        fun `Deve manter o Proprietário e não registrar falha quando o Proprietário está ativo`() {
            val proprietario = createProprietarioProjection(isDeleted = false)
            val sut = createProjection(proprietario = proprietario)

            val result = sut.toReadModel()

            result.proprietario shouldBeEqualTo
                    ProprietarioReadModel(id = proprietario.id, name = proprietario.name)
            verify(exactly = 0) { ExceptionLogger.logMasked(any()) }
        }

        @Test
        fun `Deve emitir o Imóvel sem Proprietário e registrar a falha quando o Proprietário foi excluído`() {
            val sut = createProjection(proprietario = createProprietarioProjection(isDeleted = true))

            val result = sut.toReadModel()

            result.proprietario.shouldBeNull()
            verify(exactly = 1) { ExceptionLogger.logMasked(any()) }
        }

        @Test
        fun `Deve lançar quando a relação do Proprietário não veio, pois é violação da chave estrangeira`() {
            val sut = createProjection(proprietario = null)

            assertThrows<IllegalArgumentException> { sut.toReadModel() }
            verify(exactly = 0) { ExceptionLogger.logMasked(any()) }
        }
    }

    private fun createProprietarioProjection(isDeleted: Boolean) = ProprietarioProjection(
        id = faker.random.nextLong(bound = 9999),
        name = faker.random.nextUUID(),
        isDeleted = isDeleted,
    )

    private fun createProjection(proprietario: ProprietarioProjection?) = ImovelDashboardProjection(
        id = faker.random.nextLong(bound = 9999),
        proprietarioId = faker.random.nextLong(bound = 9999),
        typeId = ImovelType.Apartment.id,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
        number = null,
        mainPictureUriString = null,
        proprietario = proprietario,
    )
}
