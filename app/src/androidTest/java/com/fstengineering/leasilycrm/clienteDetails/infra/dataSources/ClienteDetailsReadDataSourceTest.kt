package com.fstengineering.leasilycrm.clienteDetails.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            watchCliente(clienteId).assistances shouldBeEqualTo emptyList()
        }

    @Test
    fun deveEmitirNuloQuandoClienteFoiExcluido(): Unit = runBlocking(Dispatchers.Default) {
        val clienteId = db.saveFakeCliente()
        db.clienteWriteDataSource().delete(clienteId)

        sut.watchCliente(clienteId).first()
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
            .data.shouldBeNull()
    }

    private suspend fun watchCliente(clienteId: Long): ClienteReadModel =
        sut.watchCliente(clienteId).first()
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
            .data.shouldNotBeNull().shouldBeInstanceOf<ClienteReadModel>()
}
