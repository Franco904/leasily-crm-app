package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AtendimentoWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: AtendimentoWriteDataSource

    @Before
    fun setUp() {
        sut = db.atendimentoWriteDataSource()
    }

    // region save

    @Test
    fun deveSalvarAtendimentoComPreferenciasInteracoesEImoveisEnviadosQuandoAtendimentoEhCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val imovelId = db.saveFakeImovelId()

            val atendimentoId = db.saveFakeAtendimento(
                clienteId,
                adults = 3,
                interacoesTitles = listOf("Ligação"),
                imoveisEnviadosIds = listOf(imovelId),
                preferredNeighborhoods = listOf("Campeche"),
                preferredImovelCharacteristics = listOf(ImovelCharacteristic.Balcony),
            )

            val stored = getAtendimento(atendimentoId)
            stored.clienteId shouldBeEqualTo clienteId
            stored.adultsCount shouldBeEqualTo 3
            stored.interacoes.map { it.title } shouldBeEqualTo listOf("Ligação")
            stored.imoveisEnviados.map { it.id } shouldBeEqualTo listOf(imovelId)
            stored.preferredNeighborhoods shouldBeEqualTo listOf("Campeche")
            stored.preferredImovelCharacteristics shouldContainSame
                listOf(ImovelCharacteristic.Balcony)
        }

    @Test
    fun deveNaoSalvarNadaQuandoSalvamentoDeAlgumaParteFalha(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val missingImovelId = 987_654L
            val atendimento = createFakeAtendimento(
                clienteId,
                interacoesTitles = listOf("Ligação"),
                imoveisEnviadosIds = listOf(missingImovelId),
            )

            sut.save(atendimento).shouldBeInstanceOf<AppResult.Error>()

            db.atendimentoRegistrationReadDataSource().getAtendimento(atendimento.id!!)
                .shouldBeInstanceOf<AppResult.Error>()
        }

    @Test
    fun deveAtualizarAtendimentoQuandoSalvoComMesmoId(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val atendimentoId = db.saveFakeAtendimento(clienteId, adults = 1)

            db.saveFakeAtendimento(clienteId, id = atendimentoId, adults = 4)

            getAtendimento(atendimentoId).adultsCount shouldBeEqualTo 4
        }

    @Test
    fun deveMoverHospedeAntigoParaLeadQuandoAtendimentoEhSalvo(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.CheckOutDone)
            isLead(clienteId) shouldBeEqualTo false

            db.saveFakeAtendimento(clienteId)

            isLead(clienteId) shouldBeEqualTo true
        }

    // endregion

    private suspend fun getAtendimento(atendimentoId: Long): AtendimentoReadModel =
        db.atendimentoRegistrationReadDataSource().getAtendimento(atendimentoId)
            .shouldBeInstanceOf<AppResult.Success<AtendimentoReadModel>>().data

    private suspend fun isLead(clienteId: Long): Boolean? =
        (db.clienteDetailsReadDataSource().watchCliente(clienteId).first() as AppResult.Success)
            .data?.isLead
}
