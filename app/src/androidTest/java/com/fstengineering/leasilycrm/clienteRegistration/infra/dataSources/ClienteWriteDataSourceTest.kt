package com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.dataSources.ClienteDetailsReadDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeCliente
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
class ClienteWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClienteWriteDataSource

    private lateinit var clienteDetailsReadDataSource: ClienteDetailsReadDataSource

    @Before
    fun setUp() {
        sut = db.clienteWriteDataSource()
        clienteDetailsReadDataSource = db.clienteDetailsReadDataSource()
    }

    // region save

    @Test
    fun deveExibirClienteNosDetalhesComoLeadQuandoCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = faker.random.nextInt(bound = 9999).toLong() + 1
            val cliente = createFakeCliente(id = clienteId, city = faker.random.nextUUID())

            val result = sut.save(cliente)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            val clienteReadModel = watchCliente(clienteId)
            clienteReadModel.name shouldBeEqualTo cliente.name.value
            clienteReadModel.city shouldBeEqualTo cliente.city
            clienteReadModel.phone shouldBeEqualTo cliente.phone?.value
            clienteReadModel.email shouldBeEqualTo cliente.email?.value
            clienteReadModel.isLead shouldBeEqualTo true
        }

    @Test
    fun deveExibirDadosAtualizadosNosDetalhesSemCriarOutroClienteQuandoClienteEditado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = faker.random.nextInt(bound = 9999).toLong() + 1
            sut.save(createFakeCliente(id = clienteId)).shouldBeInstanceOf<AppResult.Success<*, *>>()
            val editedCliente = createFakeCliente(id = clienteId, name = faker.random.nextUUID())

            val result = sut.save(editedCliente)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            watchCliente(clienteId).name shouldBeEqualTo editedCliente.name.value
        }

    // endregion

    // region delete

    @Test
    fun deveDeixarDeExibirClienteNosDetalhesQuandoExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = faker.random.nextInt(bound = 9999).toLong() + 1
            sut.save(createFakeCliente(id = clienteId)).shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.delete(clienteId)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            clienteDetailsReadDataSource.watchCliente(clienteId).first()
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldBeNull()
        }

    // endregion

    private suspend fun watchCliente(clienteId: Long): ClienteReadModel =
        clienteDetailsReadDataSource.watchCliente(clienteId).first()
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
            .data.shouldNotBeNull().shouldBeInstanceOf<ClienteReadModel>()
}
