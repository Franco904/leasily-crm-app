package com.fstengineering.leasilycrm.testUtils.fakes

import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Atendimento
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Interacao
import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovelFile
import java.util.concurrent.atomic.AtomicLong
import kotlinx.collections.immutable.toPersistentList
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
    checkIn: AppDateTime = AppDateTimeProviderImpl().now(),
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

private val nextFakeAtendimentoId = AtomicLong(0)

/** Cria um Atendimento válido do Cliente, com as Interações e Preferências informadas. */
fun createFakeAtendimento(
    clienteId: Long,
    id: Long = nextFakeAtendimentoId.incrementAndGet(),
    isFinished: Boolean = false,
    adults: Int = 2,
    kids: Int = 1,
    babies: Int = 0,
    interacoesTitles: List<String> = emptyList(),
    imoveisEnviadosIds: List<Long> = emptyList(),
    preferredNeighborhoods: List<String> = emptyList(),
    preferredImovelCharacteristics: List<ImovelCharacteristic> = emptyList(),
): Atendimento {
    val interacoes = interacoesTitles.map {
        Interacao.parse(id = null, date = AppDateProviderImpl().now(), title = it, description = null)
            .shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<Interacao>()
    }

    return Atendimento.parse(
        id = id,
        clienteId = clienteId,
        createDateTime = null,
        preferredStartDate = null,
        preferredEndDate = null,
        hasFlexibilityForPeriod = false,
        maxAcceptedNightlyRate = null,
        adultsCount = adults,
        kidsCount = kids,
        babiesCount = babies,
        animalsCount = 0,
        bedroomsCount = 0,
        enSuitesCount = 0,
        doubleBedsCount = 0,
        singleBedsCount = 0,
        bathroomsCount = 0,
        parkingSpotsCount = 0,
        preferredSeaProximityTypeId = null,
        preferredDistanceToSea = null,
        preferredNeighborhoods = preferredNeighborhoods.toPersistentList(),
        preferredImovelCharacteristics = preferredImovelCharacteristics.toPersistentList(),
        observations = null,
        imoveisEnviadosIds = imoveisEnviadosIds.toPersistentList(),
        interacoes = interacoes.toPersistentList(),
        isFinished = isFinished,
    ).shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<Atendimento>()
}

/**
 * Salva um Atendimento do Cliente pelo `AtendimentoWriteDataSource` e devolve o seu id. Salvar de
 * novo com o mesmo [id] acrescenta Interações, Imóveis enviados e Preferências de procura.
 */
suspend fun AppDatabase.saveFakeAtendimento(
    clienteId: Long,
    id: Long = nextFakeAtendimentoId.incrementAndGet(),
    isFinished: Boolean = false,
    adults: Int = 2,
    kids: Int = 1,
    babies: Int = 0,
    interacoesTitles: List<String> = emptyList(),
    imoveisEnviadosIds: List<Long> = emptyList(),
    preferredNeighborhoods: List<String> = emptyList(),
    preferredImovelCharacteristics: List<ImovelCharacteristic> = emptyList(),
): Long {
    val atendimento = createFakeAtendimento(
        clienteId, id, isFinished, adults, kids, babies, interacoesTitles, imoveisEnviadosIds,
        preferredNeighborhoods, preferredImovelCharacteristics,
    )

    atendimentoWriteDataSource().save(atendimento).shouldBeInstanceOf<AppResult.Success<*>>()
    return id
}

/** Salva um Imóvel com foto principal e devolve o seu id e o URI da foto. */
suspend fun AppDatabase.saveFakeImovelWithMainPicture(): Pair<Long, String> {
    val imovelId = faker.random.nextInt(bound = 9999).toLong() + 1
    val mainPictureUri = faker.random.nextUUID()
    val imovel = createFakeImovel(
        proprietarioId = saveFakeProprietario(),
        id = imovelId,
        files = listOf(createFakeImovelFile(id = null, uriString = mainPictureUri, isMainPicture = true)),
    )
    imovelWriteDataSource().save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()
    return imovelId to mainPictureUri
}
