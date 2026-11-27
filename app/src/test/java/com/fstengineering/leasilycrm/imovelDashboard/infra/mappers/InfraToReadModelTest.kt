package com.fstengineering.leasilycrm.imovelDashboard.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.PeriodoOcupadoProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ProprietarioProjection
import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class InfraToReadModelTest {
    @Nested
    @DisplayName("toReadModel")
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
            val sut =
                createProjection(proprietario = createProprietarioProjection(isDeleted = true))

            val result = sut.toReadModel()

            result.proprietario.shouldBeNull()
        }

        @Test
        fun `Deve lançar quando a relação do Proprietário não veio, pois é violação da chave estrangeira`() {
            val sut = createProjection(proprietario = null)

            invoking { sut.toReadModel() } shouldThrow IllegalArgumentException::class
        }
    }

    @Nested
    @DisplayName("toValueObject")
    inner class ToValueObjectTests {
        @Test
        fun `Deve manter os dados do Período Ocupado quando o painel é montado`() {
            val sut = PeriodoOcupadoProjection(
                id = faker.random.nextLong(bound = 9999),
                tenantId = faker.random.nextLong(bound = 9999),
                checkInDateTime = AppDateTime(LocalDateTime.of(2026, 1, 5, 14, 0)),
                checkOutDateTime = AppDateTime(LocalDateTime.of(2026, 1, 10, 11, 0)),
            )

            val result = sut.toValueObject()

            result shouldBeEqualTo ImovelPeriodoOcupado(
                id = sut.id,
                tenantId = sut.tenantId,
                checkInDateTime = sut.checkInDateTime,
                checkOutDateTime = sut.checkOutDateTime,
            )
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
