package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovelPrice
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelRegistrationReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelRegistrationReadDataSource

    private lateinit var imovelWriteDataSource: ImovelWriteDataSource

    @Before
    fun setUp() {
        sut = db.imovelRegistrationReadDataSource()
        imovelWriteDataSource = db.imovelWriteDataSource()
    }

    // region getImovel

    @Test
    fun deveBuscarImovelParaEdicaoComProprietarioCaracteristicasPrecosEArquivos(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            val characteristics = ImovelCharacteristic.entries.take(2)
            val price = createFakeImovelPrice()
            val file =
                ImovelFile(id = null, uriString = faker.random.nextUUID(), isMainPicture = true)
            val imovel = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 5L,
                characteristics = characteristics,
                prices = listOf(price),
                files = listOf(file),
            )
            imovelWriteDataSource.save(imovel).shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getImovel(imovelId = 5L)

            val readModel = result
                .shouldBeInstanceOf<AppResult.Success<ImovelReadModel, ApplicationError>>().data
            readModel.id shouldBeEqualTo 5L
            readModel.proprietario?.id shouldBeEqualTo proprietarioId
            readModel.neighborhood shouldBeEqualTo imovel.neighborhood
            readModel.characteristics!! shouldContainSame characteristics
            readModel.prices!!.single().amount shouldBeEqualTo price.amount.value
            readModel.files!!.single().uriString shouldBeEqualTo file.uriString
        }

    @Test
    fun deveFalharAoBuscarImovelParaEdicaoQuandoProprietarioFoiExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            imovelWriteDataSource.save(createFakeImovel(proprietarioId = proprietarioId, id = 6L))
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
            db.proprietarioWriteDataSource().delete(proprietarioId)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getImovel(imovelId = 6L)

            result.shouldBeInstanceOf<AppResult.Error<ImovelReadModel, ApplicationError>>()
        }

    @Test
    fun deveFalharAoBuscarImovelParaEdicaoQuandoImovelFoiExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            imovelWriteDataSource.save(createFakeImovel(proprietarioId = proprietarioId, id = 7L))
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
            db.imovelSharedWriteDataSource().delete(imovelId = 7L)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getImovel(imovelId = 7L)

            result.shouldBeInstanceOf<AppResult.Error<ImovelReadModel, ApplicationError>>()
        }

    // endregion

    // region getNeighborhoodNames

    @Test
    fun deveListarBairrosSemRepeticaoEmOrdemAlfabeticaSemOsDeImoveisExcluidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            listOf(
                "Centro" to 1L,
                "Agronômica" to 2L,
                "Centro" to 3L,
                "Trindade" to 4L
            ).forEach { (bairro, id) ->
                imovelWriteDataSource
                    .save(
                        createFakeImovel(
                            proprietarioId = proprietarioId,
                            id = id,
                            neighborhood = bairro
                        )
                    )
                    .shouldBeInstanceOf<AppResult.Success<*, *>>()
            }
            db.imovelSharedWriteDataSource().delete(imovelId = 4L)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getNeighborhoodNames()

            result.shouldBeInstanceOf<AppResult.Success<List<String>, ApplicationError>>()
                .data shouldBeEqualTo listOf("Agronômica", "Centro")
        }

    // endregion
}
