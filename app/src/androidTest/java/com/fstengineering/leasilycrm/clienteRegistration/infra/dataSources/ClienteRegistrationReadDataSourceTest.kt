package com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.clienteRegistration.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeCliente
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
class ClienteRegistrationReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClienteRegistrationReadDataSource

    @Before
    fun setUp() {
        sut = db.clienteRegistrationReadDataSource()
    }

    // region getCliente

    @Test
    fun devePreencherFormularioComClienteSalvoQuandoClienteEhEditado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = faker.random.nextInt(bound = 9999).toLong() + 1
            val cliente = createFakeCliente(
                id = clienteId,
                city = faker.random.nextUUID(),
                tag = faker.random.nextUUID(),
                contactOrigin = faker.random.nextUUID(),
            )
            db.clienteWriteDataSource().save(cliente)

            val result = sut.getCliente(clienteId)

            val readModel = result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldBeInstanceOf<ClienteReadModel>()
            readModel.id shouldBeEqualTo clienteId
            readModel.name shouldBeEqualTo cliente.name.value
            readModel.city shouldBeEqualTo cliente.city
            readModel.tag shouldBeEqualTo cliente.tag
            readModel.contactOrigin shouldBeEqualTo cliente.contactOrigin
            readModel.phone shouldBeEqualTo cliente.phone?.value
            readModel.email shouldBeEqualTo cliente.email?.value
            readModel.isLead shouldBeEqualTo true
        }

    @Test
    fun deveIndicarQueClienteNaoEhLeadQuandoClienteTemEstadiaAtiva(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.Upcoming)

            val result = sut.getCliente(clienteId)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldBeInstanceOf<ClienteReadModel>().isLead shouldBeEqualTo false
        }

    @Test
    fun deveFalharQuandoClienteEhEditadoDadoQueFoiExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.clienteWriteDataSource().delete(clienteId)

            val result = sut.getCliente(clienteId)

            result.shouldBeInstanceOf<AppResult.Error<*, *>>()
        }

    // endregion

    // region sugestões de preenchimento

    @Test
    fun deveSugerirEtiquetasUsadasSemRepetirEmOrdemAlfabeticaQuandoClienteECadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente(tag = "VIP")
            db.saveFakeCliente(tag = "Amigo")
            db.saveFakeCliente(tag = "VIP")
            db.saveFakeCliente(tag = null)
            val excluido = db.saveFakeCliente(tag = "Antigo")
            db.clienteWriteDataSource().delete(excluido)

            val result = sut.getTags()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data shouldBeEqualTo listOf("Amigo", "VIP")
        }

    @Test
    fun deveSugerirCidadesUsadasSemRepetirEmOrdemAlfabeticaQuandoClienteECadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente(city = "Palhoça")
            db.saveFakeCliente(city = "Florianópolis")
            db.saveFakeCliente(city = "Palhoça")

            val result = sut.getCities()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data shouldBeEqualTo listOf("Florianópolis", "Palhoça")
        }

    @Test
    fun deveSugerirOrigensDoContatoUsadasSemRepetirEmOrdemAlfabeticaQuandoClienteECadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            db.saveFakeCliente(contactOrigin = "Instagram")
            db.saveFakeCliente(contactOrigin = "Indicação")
            db.saveFakeCliente(contactOrigin = "Instagram")

            val result = sut.getContactOrigins()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data shouldBeEqualTo listOf("Indicação", "Instagram")
        }

    // endregion
}
