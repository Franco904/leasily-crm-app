package com.fstengineering.leasilycrm.testUtils.fakes

import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.testUtils.faker
import org.amshove.kluent.shouldBeInstanceOf

fun createFakeCliente(
    id: Long? = null,
    name: String = faker.random.nextUUID(),
    city: String? = null,
    tag: String? = null,
    contactOrigin: String? = null,
    identityDocument: String? = null,
): Cliente = Cliente.parse(
    id = id,
    createDateTime = null,
    tag = tag,
    name = name,
    city = city,
    identityDocument = identityDocument,
    phone = faker.random.nextInt(bound = 999999999).toString(),
    email = "${faker.random.nextUUID()}@leasily.com",
    contactOrigin = contactOrigin,
    observations = null,
).shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<Cliente>()

/** Salva um Cliente aleatório e devolve o seu id. */
suspend fun AppDatabase.saveFakeCliente(
    id: Long = faker.random.nextInt(bound = 9999).toLong() + 1,
    name: String = faker.random.nextUUID(),
    city: String? = null,
    tag: String? = null,
    contactOrigin: String? = null,
    identityDocument: String? = null,
): Long {
    clienteWriteDataSource()
        .save(createFakeCliente(id, name, city, tag, contactOrigin, identityDocument))
        .shouldBeInstanceOf<AppResult.Success<*>>()
    return id
}

/** Salva um Imóvel aleatório (exigido pela FK da Estadia) e devolve o seu id. */
suspend fun AppDatabase.saveFakeImovelId(): Long {
    val imovelId = faker.random.nextInt(bound = 9999).toLong() + 1
    val imovel = createFakeImovel(proprietarioId = saveFakeProprietario(), id = imovelId)
    imovelWriteDataSource().save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()
    return imovelId
}

/** Insere uma Estadia do Cliente com o [status] informado e devolve o seu id. */
suspend fun AppDatabase.insertFakeEstadia(
    clienteId: Long,
    imovelId: Long,
    status: StayStatus,
    checkIn: AppDateTime = AppDateTime.now(),
): Long = stayDao().insertOrUpdate(
    StayEntity(
        id = null,
        createDateTime = checkIn,
        updateDateTime = checkIn,
        vehicleMake = null,
        vehicleColor = null,
        vehicleLicensePlate = null,
        guests = null,
        checkInDateTime = checkIn,
        checkOutDateTime = checkIn.plusDays(3),
        isAutoCheckIn = null,
        isAutoCheckOut = null,
        totalPrice = null,
        commission = null,
        observations = null,
        isDeleted = false,
        status = status.id,
        imovelId = imovelId,
        tenantId = clienteId,
        contractVersion = 1,
    )
)

/** Insere um Atendimento do Cliente e devolve o seu id. */
suspend fun AppDatabase.insertFakeAtendimento(
    clienteId: Long,
    isFinished: Boolean = false,
    adults: Int = 2,
    kids: Int = 1,
    babies: Int = 0,
): Long = assistanceDao().insertOrUpdate(
    AssistanceEntity(
        id = null,
        createDateTime = AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        searchedStartDate = null,
        searchedEndDate = null,
        hasFlexibilityForPeriod = false,
        maxAcceptedNightlyRate = null,
        adults = adults,
        kids = kids,
        babies = babies,
        animals = 0,
        bedrooms = 0,
        enSuites = 0,
        doubleBeds = 0,
        singleBeds = 0,
        bathrooms = 0,
        parkingSpots = 0,
        distanceToSea = null,
        observations = null,
        isFinished = isFinished,
        isDeleted = false,
        imovelSeaProximityTypeId = null,
        clienteId = clienteId,
        contractVersion = 1,
    )
)

/** Registra uma Interação no Atendimento. */
suspend fun AppDatabase.insertFakeInteracao(
    atendimentoId: Long,
    title: String = faker.random.nextUUID(),
) {
    assistanceInteractionDao().insertOrUpdate(
        AssistanceInteractionEntity(
            id = null,
            date = AppDate.now(),
            title = title,
            description = null,
            assistanceId = atendimentoId,
        )
    )
}

/** Envia o Imóvel ao Lead no Atendimento. */
suspend fun AppDatabase.sendFakeImovel(atendimentoId: Long, imovelId: Long) {
    assistanceSentImovelCrossRefDao().insertOrUpdate(
        AssistanceSentImovelCrossRefEntity(assistanceId = atendimentoId, imovelId = imovelId)
    )
}

/** Salva um Imóvel com foto principal e devolve o seu id e o URI da foto. */
suspend fun AppDatabase.saveFakeImovelWithMainPicture(): Pair<Long, String> {
    val imovelId = faker.random.nextInt(bound = 9999).toLong() + 1
    val mainPictureUri = faker.random.nextUUID()
    val imovel = createFakeImovel(
        proprietarioId = saveFakeProprietario(),
        id = imovelId,
        files = listOf(ImovelFile(id = null, uriString = mainPictureUri, isMainPicture = true)),
    )
    imovelWriteDataSource().save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()
    return imovelId to mainPictureUri
}

/** Registra no Atendimento o bairro e a característica de Imóvel que o Lead procura. */
suspend fun AppDatabase.insertFakePreferencias(
    atendimentoId: Long,
    neighborhood: String,
    characteristic: ImovelCharacteristic,
) {
    assistanceSearchedNeighborhoodDao().insertOrUpdate(
        AssistancePreferredNeighborhoodEntity(neighborhood = neighborhood, assistanceId = atendimentoId)
    )
    assistanceCharacteristicAssistanceCrossRefDao().insertOrUpdate(
        AssistanceImovelCharacteristicCrossRefEntity(
            assistanceId = atendimentoId,
            imovelCharacteristicId = characteristic.id,
        )
    )
}
