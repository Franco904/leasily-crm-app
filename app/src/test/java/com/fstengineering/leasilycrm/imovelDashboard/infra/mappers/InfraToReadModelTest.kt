package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ProprietarioProjection
import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InfraToReadModelTest {
    @Nested
    @DisplayName("ImovelDashboardProjection.toReadModel")
    inner class ToReadModelTests {
        @Test
        fun `Deve manter o Proprietário quando o Proprietário está ativo`() {
            val proprietario = createProprietarioProjection(isDeleted = false)
            val sut = createProjection(proprietario = proprietario)

            val result = sut.toReadModel()

            result.proprietario shouldBeEqualTo
                    ProprietarioReadModel(id = proprietario.id, name = proprietario.name)
        }

        @Test
        fun `Deve emitir o Imóvel sem Proprietário quando o Proprietário foi excluído`() {
            val sut = createProjection(proprietario = createProprietarioProjection(isDeleted = true))

            val result = sut.toReadModel()

            result.proprietario.shouldBeNull()
        }

        @Test
        fun `Deve lançar quando a relação do Proprietário não veio, pois é violação da chave estrangeira`() {
            val sut = createProjection(proprietario = null)

            assertThrows<IllegalArgumentException> { sut.toReadModel() }
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
