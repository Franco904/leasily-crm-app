package com.fstengineering.leasilycrm.imovelRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.projections.ImovelProprietarioProjection
import com.fstengineering.leasilycrm.imovelRegistration.infra.projections.ImovelRegistrationProjection
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
    @DisplayName("ImovelRegistrationProjection.toReadModel")
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

    private fun createProprietarioProjection(isDeleted: Boolean) = ImovelProprietarioProjection(
        id = faker.random.nextLong(bound = 9999),
        name = faker.random.nextUUID(),
        isDeleted = isDeleted,
    )

    private fun createProjection(proprietario: ImovelProprietarioProjection?): ImovelRegistrationProjection {
        val now = AppDateTime(LocalDateTime.of(2026, 3, 10, 10, 0))

        return ImovelRegistrationProjection(
            imovel = ImovelOrm(
                id = faker.random.nextLong(bound = 9999),
                createDateTime = now,
                updateDateTime = now,
                neighborhood = faker.random.nextUUID(),
                streetAddress = faker.random.nextUUID(),
                cepPostalCode = null,
                number = null,
                addressLine2 = null,
                parkingSpotNumber = null,
                parkingBasementLevelNumber = null,
                hobbyBoxNumber = null,
                bedrooms = 1,
                enSuites = 0,
                doubleBeds = 1,
                singleBeds = 0,
                bathrooms = 1,
                parkingSpots = 0,
                airConditioners = 0,
                maxHospedes = 2,
                distanceToSea = null,
                includedItems = null,
                hasIncludedItems = false,
                cleaningServiceTax = null,
                hasCleaningServiceIncluded = false,
                observations = null,
                isDeleted = false,
                proprietarioId = proprietario?.id ?: faker.random.nextLong(bound = 9999),
                typeId = ImovelType.Apartment.id,
                seaProximityTypeId = null,
                contractVersion = 1,
            ),
            proprietario = proprietario,
            characteristics = emptyList(),
            prices = emptyList(),
            files = emptyList(),
        )
    }
}
