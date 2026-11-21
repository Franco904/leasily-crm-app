package com.fstengineering.leasilycrm.testUtils.fakes

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimal
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import org.amshove.kluent.shouldBeInstanceOf
import java.time.LocalDate

/** Salva um Proprietário aleatório (exigido pela FK do Imóvel) e devolve o seu id. */
suspend fun AppDatabase.saveFakeProprietario(
    name: String = faker.random.nextUUID(),
    proprietarioId: Long = faker.random.nextInt(bound = 9999).toLong() + 1,
): Long {
    val proprietario = Proprietario.parse(
        id = proprietarioId,
        createDateTime = null,
        name = name,
        phone = null,
        email = null,
        paymentKey = null,
        files = persistentListOf(),
    ).shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<Proprietario>()
    proprietarioWriteDataSource().save(proprietario)
        .shouldBeInstanceOf<AppResult.Success<*>>()
    return proprietarioId
}

fun createFakeImovel(
    proprietarioId: Long,
    id: Long? = null,
    neighborhood: String = faker.random.nextUUID(),
    streetAddress: String = faker.random.nextUUID(),
    prices: List<ImovelPrice> = emptyList(),
    characteristics: List<ImovelCharacteristic> = emptyList(),
    files: List<ImovelFile> = emptyList(),
): Imovel = Imovel.parse(
    id = id,
    proprietarioId = proprietarioId,
    type = ImovelType.Apartment,
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    characteristics = persistentListOf<ImovelCharacteristic>().addAll(characteristics),
    prices = persistentListOf<ImovelPrice>().addAll(prices),
    files = persistentListOf<ImovelFile>().addAll(files),
).shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<Imovel>()

fun createFakeImovelPrice(
    id: Long? = null,
    amount: AppDecimal = (faker.random.nextInt(bound = 9999) + 1).toDecimal(),
): ImovelPrice = ImovelPrice.parse(
    id = id,
    startDate = AppDate(LocalDate.of(2030, 1, 1)),
    endDate = AppDate(LocalDate.of(2030, 1, 31)),
    amount = amount,
).shouldBeInstanceOf<AppResult.Success<*>>().data.shouldBeInstanceOf<ImovelPrice>()
