package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimal
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelWriteDataSource

    private lateinit var imovelDao: ImovelDao

    @Before
    fun setUp() {
        sut = db.imovelWriteDataSource()
        imovelDao = db.imovelDao()
    }

    // region save

    @Test
    fun deveNaoSalvarImovelQuandoSalvamentoDeAlgumPrecoFalha(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = saveFakeProprietario()
            makePriceInsertionFail()
            val imovel = createFakeImovel(
                proprietarioId = proprietarioId,
                prices = persistentListOf(createFakeImovelPrice()),
            )

            val result = sut.save(imovel)

            result.shouldBeInstanceOf<AppResult.Error<*, *>>()
            imovelDao.getAll().shouldBeEmpty()
        }

    @Test
    fun devePersistirCaracteristicasDoImovelQuandoDadosSaoValidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val characteristics = ImovelCharacteristic.entries.shuffled().take(3)
            val imovel = createFakeImovel(
                proprietarioId = saveFakeProprietario(),
                characteristics = characteristics,
            )

            val result = sut.save(imovel)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            imovelDao.getAll().single().characteristics.map { it.imovelCharacteristicId }
                .shouldContainSame(characteristics.map { it.id })
        }

    @Test
    fun devePersistirArquivosDoImovelQuandoDadosSaoValidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val files = listOf(
                ImovelFile(id = null, uriString = faker.random.nextUUID(), isMainPicture = true),
                ImovelFile(id = null, uriString = faker.random.nextUUID(), isMainPicture = false),
            )
            val imovel = createFakeImovel(proprietarioId = saveFakeProprietario(), files = files)

            val result = sut.save(imovel)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            imovelDao.getAll().single().files
                .map { it.imovelFileUri to it.isMainPicture }
                .shouldContainSame(files.map { it.uriString to it.isMainPicture })
        }

    @Test
    fun deveAtualizarImovelExistenteEmVezDeCriarOutroQuandoSaveRecebeImovelComId(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = saveFakeProprietario()
            sut.save(
                createFakeImovel(
                    proprietarioId = proprietarioId,
                    prices = listOf(createFakeImovelPrice()),
                )
            ).shouldBeInstanceOf<AppResult.Success<*, *>>()
            val saved = imovelDao.getAll().single()
            val savedPrice = saved.prices.single()
            val editedNeighborhood = faker.random.nextUUID()
            val editedAmount = (faker.random.nextInt(bound = 9999) + 1).toDecimal()
            val edited = createFakeImovel(
                id = saved.imovel.id,
                proprietarioId = proprietarioId,
                neighborhood = editedNeighborhood,
                prices = listOf(createFakeImovelPrice(id = savedPrice.id, amount = editedAmount)),
            )

            val result = sut.save(edited)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            val imoveis = imovelDao.getAll()
            imoveis.shouldHaveSize(1)
            imoveis.single().imovel.neighborhood shouldBeEqualTo editedNeighborhood
            imoveis.single().imovel.id shouldBeEqualTo saved.imovel.id
            val price = imoveis.single().prices.single()
            price.id shouldBeEqualTo savedPrice.id
            price.imovelId shouldBeEqualTo saved.imovel.id
            price.amount shouldBeEqualTo editedAmount
        }

    // endregion

    /** Faz o banco recusar qualquer inserção de Preço, como faria uma violação de constraint. */
    private fun makePriceInsertionFail() {
        db.openHelper.writableDatabase.execSQL(
            """
            CREATE TRIGGER fail_price_insertion BEFORE INSERT ON property_prices
            BEGIN SELECT RAISE(ABORT, 'price insertion failed'); END
            """
        )
    }

    private suspend fun saveFakeProprietario(): Long {
        val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
        val proprietario = Proprietario.parse(
            id = proprietarioId,
            createDateTime = null,
            name = faker.random.nextUUID(),
            phone = null,
            email = null,
            paymentKey = null,
            files = persistentListOf(),
        ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()
        db.proprietarioWriteDataSource().save(proprietario)
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
        return proprietarioId
    }

    private fun createFakeImovel(
        proprietarioId: Long,
        id: Long? = null,
        neighborhood: String = faker.random.nextUUID(),
        prices: List<ImovelPrice> = emptyList(),
        characteristics: List<ImovelCharacteristic> = emptyList(),
        files: List<ImovelFile> = emptyList(),
    ): Imovel = Imovel.parse(
        id = id,
        proprietarioId = proprietarioId,
        type = ImovelType.Apartment,
        neighborhood = neighborhood,
        streetAddress = faker.random.nextUUID(),
        characteristics = persistentListOf<ImovelCharacteristic>().addAll(characteristics),
        prices = persistentListOf<ImovelPrice>().addAll(prices),
        files = persistentListOf<ImovelFile>().addAll(files),
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Imovel>()

    private fun createFakeImovelPrice(
        id: Long? = null,
        amount: AppDecimal = (faker.random.nextInt(bound = 9999) + 1).toDecimal(),
    ): ImovelPrice = ImovelPrice.parse(
        id = id,
        startDate = AppDate(LocalDate.of(2030, 1, 1)),
        endDate = AppDate(LocalDate.of(2030, 1, 31)),
        amount = amount,
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<ImovelPrice>()
}
