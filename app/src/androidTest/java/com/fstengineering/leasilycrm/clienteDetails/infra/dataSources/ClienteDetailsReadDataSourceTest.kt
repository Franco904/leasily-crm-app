package com.fstengineering.leasilycrm.clienteDetails.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeInteracao
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakePreferencias
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelWithMainPicture
import com.fstengineering.leasilycrm.testUtils.fakes.sendFakeImovel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ClienteDetailsReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClienteDetailsReadDataSource

    @Before
    fun setUp() {
        sut = db.clienteDetailsReadDataSource()
    }

    @Test
    fun deveExibirDadosDeContatoQuandoDetalhesDoClienteSaoAbertos(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente(name = "Maria", city = "Palhoça", tag = "VIP")

            val cliente = watchCliente(clienteId)

            cliente.name shouldBeEqualTo "Maria"
            cliente.city shouldBeEqualTo "Palhoça"
            cliente.tag shouldBeEqualTo "VIP"
            cliente.isLead shouldBeEqualTo true
            cliente.isOld shouldBeEqualTo false
        }

    @Test
    fun deveExibirDocumentoEOrigemDoContatoQuandoDetalhesDoClienteSaoAbertos(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente(
                identityDocument = "123.456.789-00",
                contactOrigin = "Instagram",
            )

            val cliente = watchCliente(clienteId)

            cliente.identityDocument shouldBeEqualTo "123.456.789-00"
            cliente.contactOrigin shouldBeEqualTo "Instagram"
        }

    @Test
    fun deveExibirAtendimentoAtualEEstadiasPassadasQuandoClienteTemHistorico(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.insertFakeAtendimento(clienteId, isFinished = false)
            val estadiaId =
                db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.CheckOutDone)

            val cliente = watchCliente(clienteId)

            cliente.assistances.single().id shouldBeEqualTo atendimentoId
            cliente.assistances.single().isFinished shouldBeEqualTo false
            cliente.stays.single().id shouldBeEqualTo estadiaId
            cliente.stays.single().isCheckOutDone shouldBeEqualTo true
            cliente.isLead shouldBeEqualTo true
        }

    @Test
    fun deveMarcarClienteComoHospedeAntigoQuandoSoTemEstadiasFinalizadas(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.CheckOutDone)

            val cliente = watchCliente(clienteId)

            cliente.isOld shouldBeEqualTo true
            cliente.isLead shouldBeEqualTo false
        }

    @Test
    fun deveDeixarDeExibirAtendimentoQuandoAtendimentoEExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.insertFakeAtendimento(clienteId, isFinished = false)

            db.assistanceWriteDataSource().delete(atendimentoId)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            watchCliente(clienteId).assistances shouldBeEqualTo emptyList()
        }

    @Test
    fun deveEmitirNuloQuandoClienteFoiExcluido(): Unit = runBlocking(Dispatchers.Default) {
        val clienteId = db.saveFakeCliente()
        db.clienteWriteDataSource().delete(clienteId)

        sut.watchCliente(clienteId).first()
            .shouldBeInstanceOf<AppResult.Success<*>>()
            .data.shouldBeNull()
    }

    @Test
    fun deveEmitirDeNovoQuandoInteracaoEhRegistradaNoAtendimento(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.insertFakeAtendimento(clienteId, isFinished = false)
            val emissions = Channel<ClienteReadModel?>(Channel.UNLIMITED)
            val collecting = launch {
                sut.watchCliente(clienteId).collect { result ->
                    emissions.send((result as AppResult.Success).data)
                }
            }
            try {
                withTimeout(2_000) { emissions.receive() }

                db.insertFakeInteracao(atendimentoId, title = "Ligação")

                val cliente = withTimeout(2_000) { emissions.receive() }.shouldNotBeNull()
                cliente.assistances.single().interactions.map { it.title } shouldBeEqualTo
                    listOf("Ligação")
            } finally {
                collecting.cancel()
            }
        }

    @Test
    fun deveExibirFotoDoImovelEnviadoEPreferenciasDoAtendimento(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.insertFakeAtendimento(clienteId, isFinished = false)
            val (imovelId, mainPictureUri) = db.saveFakeImovelWithMainPicture()
            db.sendFakeImovel(atendimentoId, imovelId)
            db.insertFakePreferencias(atendimentoId, "Campeche", ImovelCharacteristic.Balcony)

            val atendimento = watchCliente(clienteId).assistances.single()

            atendimento.sentImoveis.single().mainPictureUriString shouldBeEqualTo mainPictureUri
            atendimento.preferredNeighborhoods shouldBeEqualTo listOf("Campeche")
            atendimento.preferredImovelCharacteristics shouldBeEqualTo
                listOf(ImovelCharacteristic.Balcony)
        }

    @Test
    fun deveNaoExibirImovelEnviadoQuandoImovelEExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.insertFakeAtendimento(clienteId, isFinished = false)
            val keptImovelId = db.saveFakeImovelId()
            val deletedImovelId = db.saveFakeImovelId()
            db.sendFakeImovel(atendimentoId, keptImovelId)
            db.sendFakeImovel(atendimentoId, deletedImovelId)
            db.imovelSharedWriteDataSource().delete(deletedImovelId)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            val cliente = watchCliente(clienteId)

            cliente.assistances.single().sentImoveis.map { it.id } shouldBeEqualTo
                listOf(keptImovelId)
        }

    @Test
    fun deveNaoExibirEstadiaQuandoEstadiaEExcluida(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val imovelId = db.saveFakeImovelId()
            val keptEstadiaId = db.insertFakeEstadia(clienteId, imovelId, StayStatus.CheckOutDone)
            val deletedEstadiaId = db.insertFakeEstadia(clienteId, imovelId, StayStatus.CheckOutDone)
            db.stayDao().delete(deletedEstadiaId)

            val cliente = watchCliente(clienteId)

            cliente.stays.map { it.id } shouldBeEqualTo listOf(keptEstadiaId)
        }

    private suspend fun watchCliente(clienteId: Long): ClienteReadModel =
        sut.watchCliente(clienteId).first()
            .shouldBeInstanceOf<AppResult.Success<*>>()
            .data.shouldNotBeNull().shouldBeInstanceOf<ClienteReadModel>()
}
