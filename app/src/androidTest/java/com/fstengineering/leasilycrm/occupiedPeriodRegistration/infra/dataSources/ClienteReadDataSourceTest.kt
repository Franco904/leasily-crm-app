package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ClienteReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClienteReadDataSource

    @Before
    fun setUp() {
        sut = db.occupiedPeriodRegistrationClienteReadDataSource()
    }

    @Test
    fun deveListarSoClientesSemEstadiaAtivaEmOrdemAlfabeticaQuandoHospedeEhSelecionado(): Unit =
        runBlocking(Dispatchers.Default) {
            val lead = db.saveFakeCliente(name = "Bruno")
            val antigo = db.saveFakeCliente(name = "Alice")
            db.insertFakeEstadia(antigo, db.saveFakeImovelId(), StayStatus.CheckOutDone)
            val comEstadiaAtiva = db.saveFakeCliente(name = "Carlos")
            db.insertFakeEstadia(comEstadiaAtiva, db.saveFakeImovelId(), StayStatus.Upcoming)

            val result = sut.getClientesWithoutEstadiaAtiva(matching = null, currentClienteId = null)

            result.shouldBeInstanceOf<AppResult.Success<*>>().data
                .let { (it as List<*>).filterIsInstance<TenantReadModel>() }
                .map { it.id } shouldBeEqualTo listOf(antigo, lead)
        }

    @Test
    fun deveIncluirOHospedeDaEstadiaEditadaQuandoHospedeEhSelecionado(): Unit =
        runBlocking(Dispatchers.Default) {
            val comEstadiaAtiva = db.saveFakeCliente(name = "Carlos")
            db.insertFakeEstadia(comEstadiaAtiva, db.saveFakeImovelId(), StayStatus.Upcoming)

            val result = sut.getClientesWithoutEstadiaAtiva(
                matching = null,
                currentClienteId = comEstadiaAtiva,
            )

            result.shouldBeInstanceOf<AppResult.Success<*>>().data
                .let { (it as List<*>).filterIsInstance<TenantReadModel>() }
                .map { it.id } shouldBeEqualTo listOf(comEstadiaAtiva)
        }

    @Test
    fun deveBuscarClientePorIdQuandoEstadiaEhEditada(): Unit = runBlocking(Dispatchers.Default) {
        val clienteId = db.saveFakeCliente(name = "Maria", city = "Palhoça")

        val result = sut.getCliente(clienteId)

        result.shouldBeInstanceOf<AppResult.Success<*>>().data shouldBeEqualTo
                TenantReadModel(id = clienteId, name = "Maria", city = "Palhoça")
    }
}
