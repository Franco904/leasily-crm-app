package com.fstengineering.leasilycrm.clientes.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ClientesReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClientesReadDataSource

    @Before
    fun setUp() {
        sut = db.clientesReadDataSource()
    }

    // region watchClientes

    @Test
    fun deveListarSoLeadsQuandoFiltradoPorLead(): Unit = runBlocking(Dispatchers.Default) {
        val lead = db.saveFakeCliente()
        val hospede = db.saveFakeCliente()
        db.insertFakeEstadia(hospede, db.saveFakeImovelId(), StayStatus.Upcoming)
        val hospedeAntigo = db.saveFakeCliente()
        db.insertFakeEstadia(hospedeAntigo, db.saveFakeImovelId(), StayStatus.CheckOutDone)

        val clientes = sut.watchClientes(status = ClienteStatus.Lead).firstSuccess()

        clientes.map { it.id } shouldBeEqualTo listOf(lead)
    }

    @Test
    fun deveListarSoHospedesComEstadiaAtivaQuandoFiltradoPorHospede(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente()
            val hospede = db.saveFakeCliente()
            val estadiaId = db.insertFakeEstadia(hospede, db.saveFakeImovelId(), StayStatus.CheckInDone)
            val hospedeAntigo = db.saveFakeCliente()
            db.insertFakeEstadia(hospedeAntigo, db.saveFakeImovelId(), StayStatus.CheckOutDone)

            val clientes = sut.watchClientes(status = ClienteStatus.Hospede).firstSuccess()

            clientes.single().id shouldBeEqualTo hospede
            clientes.single().hasUnfinishedStay shouldBeEqualTo true
            clientes.single().currentStayId shouldBeEqualTo estadiaId
        }

    @Test
    fun deveListarSoHospedesAntigosQuandoFiltradoPorHospedeAntigo(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente()
            val hospedeAntigo = db.saveFakeCliente()
            db.insertFakeEstadia(hospedeAntigo, db.saveFakeImovelId(), StayStatus.CheckOutDone)

            val clientes = sut.watchClientes(status = ClienteStatus.HospedeAntigo).firstSuccess()

            clientes.map { it.id } shouldBeEqualTo listOf(hospedeAntigo)
        }

    @Test
    fun deveTratarHospedeAntigoComoLeadQuandoTemAtendimentoNaoFinalizado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.CheckOutDone)
            db.insertFakeAtendimento(clienteId, isFinished = false)

            val leads = sut.watchClientes(status = ClienteStatus.Lead).firstSuccess()

            leads.single().id shouldBeEqualTo clienteId
        }

    @Test
    fun deveExibirHospedesDoAtendimentoNaoFinalizadoQuandoLeadEhListado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeAtendimento(clienteId, adults = 2, kids = 1, babies = 1)

            val lead = sut.watchClientes(status = ClienteStatus.Lead).firstSuccess().single()

            lead.hasUnfinishedAssistance shouldBeEqualTo true
            lead.hospedesCount shouldBeEqualTo 4
        }

    @Test
    fun deveOmitirClienteExcluidoQuandoClientesSaoListados(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.clienteWriteDataSource().delete(clienteId)

            sut.watchClientes().firstSuccess() shouldBeEqualTo emptyList()
        }

    @Test
    fun deveBuscarPorNomeOuCidadeSemDiferenciarMaiusculasQuandoHaTextoDeBusca(): Unit =
        runBlocking(Dispatchers.Default) {
            val porNome = db.saveFakeCliente(name = "Maria Silva")
            val porCidade = db.saveFakeCliente(name = "João", city = "Silveira")
            db.saveFakeCliente(name = "Pedro", city = "Palhoça")

            val clientes = sut.watchClientes(matching = "SILV").firstSuccess()

            clientes.map { it.id }.toSet() shouldBeEqualTo setOf(porNome, porCidade)
        }

    @Test
    fun deveTratarPercentualDaBuscaComoTextoLiteralQuandoHaTextoDeBusca(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente(name = "Ana")
            val comPercentual = db.saveFakeCliente(name = "100% Ana")

            val clientes = sut.watchClientes(matching = "%").firstSuccess()

            clientes.map { it.id } shouldBeEqualTo listOf(comPercentual)
        }

    @Test
    fun deveOrdenarPorNomeQuandoOrdenacaoEPorNome(): Unit = runBlocking(Dispatchers.Default) {
        val bruno = db.saveFakeCliente(name = "Bruno")
        val alice = db.saveFakeCliente(name = "alice")

        val clientes = sut.watchClientes(orderTypeId = 1).firstSuccess()

        clientes.map { it.id } shouldBeEqualTo listOf(alice, bruno)
    }

    @Test
    fun deveOrdenarPelaAtualizacaoMaisRecenteQuandoOrdenacaoEPelaUltimaAtualizacao(): Unit =
        runBlocking(Dispatchers.Default) {
            val primeiro = db.saveFakeCliente(name = "Alice")
            delay(20)
            val segundo = db.saveFakeCliente(name = "Bruno")

            val clientes = sut.watchClientes(orderTypeId = 2).firstSuccess()

            clientes.map { it.id } shouldBeEqualTo listOf(segundo, primeiro)
        }

    // endregion

    // region getLeads

    @Test
    fun deveListarLeadsQueContemOTextoQuandoLeadsSaoBuscados(): Unit =
        runBlocking(Dispatchers.Default) {
            val lead = db.saveFakeCliente(name = "Maria Silva")
            db.saveFakeCliente(name = "Pedro")
            val hospede = db.saveFakeCliente(name = "Silvio")
            db.insertFakeEstadia(hospede, db.saveFakeImovelId(), StayStatus.Upcoming)

            val result = sut.getLeads(matching = "silv", orderTypeId = 1)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>().data
                .let { (it as List<*>).filterIsInstance<ClienteReadModel>() }
                .map { it.id } shouldBeEqualTo listOf(lead)
        }

    // endregion

    private suspend fun kotlinx.coroutines.flow.Flow<AppResult<List<ClienteReadModel>, *>>.firstSuccess() =
        first().shouldBeInstanceOf<AppResult.Success<*, *>>().data
            .let { (it as List<*>).filterIsInstance<ClienteReadModel>() }
}
