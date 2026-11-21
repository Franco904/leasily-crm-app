package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelWithMainPicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AtendimentoRegistrationReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: AtendimentoRegistrationReadDataSource

    @Before
    fun setUp() {
        sut = db.atendimentoRegistrationReadDataSource()
    }

    // region getAtendimento

    @Test
    fun deveBuscarAtendimentoParaEdicaoComNomeDoClienteEFotoDoImovelEnviado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente(name = "Maria")
            val (imovelId, mainPictureUri) = db.saveFakeImovelWithMainPicture()
            val atendimentoId =
                db.saveFakeAtendimento(clienteId, imoveisEnviadosIds = listOf(imovelId))

            val stored = sut.getAtendimento(atendimentoId)
                .shouldBeInstanceOf<AppResult.Success<AtendimentoReadModel>>().data

            stored.clienteName shouldBeEqualTo "Maria"
            stored.imoveisEnviados.single().mainPictureUriString shouldBeEqualTo mainPictureUri
        }

    @Test
    fun deveNaoTrazerImovelEnviadoQuandoImovelFoiExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val keptImovelId = db.saveFakeImovelId()
            val deletedImovelId = db.saveFakeImovelId()
            val atendimentoId = db.saveFakeAtendimento(
                clienteId,
                imoveisEnviadosIds = listOf(keptImovelId, deletedImovelId),
            )
            db.imovelSharedWriteDataSource().delete(deletedImovelId)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            val stored = sut.getAtendimento(atendimentoId)
                .shouldBeInstanceOf<AppResult.Success<AtendimentoReadModel>>().data

            stored.imoveisEnviados.map { it.id } shouldBeEqualTo listOf(keptImovelId)
        }

    @Test
    fun deveFalharAoBuscarAtendimentoQuandoAtendimentoFoiExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val atendimentoId = db.saveFakeAtendimento(db.saveFakeCliente())
            db.atendimentoWriteDataSource().delete(atendimentoId)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            sut.getAtendimento(atendimentoId).shouldBeInstanceOf<AppResult.Error>()
        }

    // endregion

    // region getNeighborhoodNames

    @Test
    fun deveListarBairrosProcuradosSemRepeticaoEmOrdemAlfabetica(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.saveFakeAtendimento(clienteId, preferredNeighborhoods = listOf("Trindade", "Centro"))

            val result = sut.getNeighborhoodNames()

            result.shouldBeInstanceOf<AppResult.Success<List<String>>>()
                .data shouldBeEqualTo listOf("Centro", "Trindade")
        }

    // endregion
}
