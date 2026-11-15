package com.fstengineering.leasilycrm.imoveis.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImoveisReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImoveisReadDataSource

    private lateinit var imovelWriteDataSource: ImovelWriteDataSource
    private lateinit var imovelSharedWriteDataSource: ImovelSharedWriteDataSource

    @Before
    fun setUp() {
        sut = db.imoveisReadDataSource()
        imovelWriteDataSource = db.imovelWriteDataSource()
        imovelSharedWriteDataSource = db.imovelSharedWriteDataSource()
    }

    // region watchImoveis

    @Test
    fun deveListarImoveisEmOrdemDeBairroSemOsExcluidosEComFotoPrincipal(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            val mainPictureUri = faker.random.nextUUID()
            val centro = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 1L,
                neighborhood = "Centro",
                files = listOf(
                    ImovelFile(id = null, uriString = mainPictureUri, isMainPicture = true),
                    ImovelFile(
                        id = null,
                        uriString = faker.random.nextUUID(),
                        isMainPicture = false
                    ),
                ),
            )
            val agronomica = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 2L,
                neighborhood = "Agronômica"
            )
            val trindade = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 3L,
                neighborhood = "Trindade"
            )
            listOf(centro, agronomica, trindade).forEach {
                imovelWriteDataSource.save(it).shouldBeInstanceOf<AppResult.Success<*, *>>()
            }
            imovelSharedWriteDataSource.delete(imovelId = 3L)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.watchImoveis().first()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>().data shouldBeEqualTo listOf(
                ImovelReadModel(
                    id = 2L,
                    mainPictureUriString = null,
                    type = ImovelType.Apartment,
                    neighborhood = "Agronômica",
                    streetAddress = agronomica.streetAddress,
                    number = null,
                ),
                ImovelReadModel(
                    id = 1L,
                    mainPictureUriString = mainPictureUri,
                    type = ImovelType.Apartment,
                    neighborhood = "Centro",
                    streetAddress = centro.streetAddress,
                    number = null,
                ),
            )
        }

    // endregion

    // region getImoveis

    @Test
    fun deveListarApenasImoveisCujoBairroOuEnderecoContemOTextoBuscado(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            val centro = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 1L,
                neighborhood = "Centro",
                streetAddress = "Rua das Flores",
            )
            val trindade = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 2L,
                neighborhood = "Trindade",
                streetAddress = "Rua do Sol",
            )
            val agronomica = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 3L,
                neighborhood = "Agronômica",
                streetAddress = "Avenida Centro Norte",
            )
            listOf(centro, trindade, agronomica).forEach {
                imovelWriteDataSource.save(it).shouldBeInstanceOf<AppResult.Success<*, *>>()
            }

            val byNeighborhoodOrStreet = sut.getImoveis(matching = "centro")
            val byStreet = sut.getImoveis(matching = "flores")

            byNeighborhoodOrStreet.shouldBeInstanceOf<AppResult.Success<List<ImovelReadModel>, ApplicationError>>()
                .data.map { it.id } shouldBeEqualTo listOf(3L, 1L)
            byStreet.shouldBeInstanceOf<AppResult.Success<List<ImovelReadModel>, ApplicationError>>()
                .data.map { it.id } shouldBeEqualTo listOf(1L)
        }

    @Test
    fun deveListarTodosOsImoveisQuandoNaoHaTextoBuscado(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            listOf(
                createFakeImovel(proprietarioId = proprietarioId, id = 1L, neighborhood = "Centro"),
                createFakeImovel(
                    proprietarioId = proprietarioId,
                    id = 2L,
                    neighborhood = "Agronômica"
                ),
            ).forEach {
                imovelWriteDataSource.save(it).shouldBeInstanceOf<AppResult.Success<*, *>>()
            }

            val result = sut.getImoveis(matching = null)

            result.shouldBeInstanceOf<AppResult.Success<List<ImovelReadModel>, ApplicationError>>()
                .data.map { it.id } shouldBeEqualTo listOf(2L, 1L)
        }

    @Test
    fun deveTratarPorcentagemDaBuscaComoTextoLiteral(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            listOf(
                createFakeImovel(
                    proprietarioId = proprietarioId,
                    id = 1L,
                    neighborhood = "100% Centro"
                ),
                createFakeImovel(
                    proprietarioId = proprietarioId,
                    id = 2L,
                    neighborhood = "Trindade"
                ),
            ).forEach {
                imovelWriteDataSource.save(it).shouldBeInstanceOf<AppResult.Success<*, *>>()
            }

            val result = sut.getImoveis(matching = "%")

            result.shouldBeInstanceOf<AppResult.Success<List<ImovelReadModel>, ApplicationError>>()
                .data.map { it.id } shouldBeEqualTo listOf(1L)
        }

    // endregion
}
