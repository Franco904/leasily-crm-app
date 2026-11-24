package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.insertFakeEstadia
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeAtendimento
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeImovelId
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
class AtendimentoSharedWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: AtendimentoSharedWriteDataSource

    @Before
    fun setUp() {
        sut = db.atendimentoSharedWriteDataSource()
    }

    @Test
    fun deveFinalizarAtendimentoQuandoClienteTemAtendimentoNaoFinalizado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.saveFakeAtendimento(clienteId, isFinished = false)

            sut.finishOpen(clienteId).shouldBeInstanceOf<AppResult.Success<*>>()

            isAtendimentoFinished(clienteId) shouldBeEqualTo true
        }

    @Test
    fun deveReabrirAtendimentoQuandoClienteTemAtendimentoFinalizado(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.saveFakeAtendimento(clienteId, isFinished = true)

            sut.unfinish(clienteId).shouldBeInstanceOf<AppResult.Success<*>>()

            isAtendimentoFinished(clienteId) shouldBeEqualTo false
        }

    @Test
    fun deveManterAtendimentoDeOutroClienteQuandoFinalizaOAtendimentoDoCliente(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            val otherClienteId = db.saveFakeCliente()
            db.saveFakeAtendimento(clienteId)
            db.saveFakeAtendimento(otherClienteId)

            sut.finishOpen(clienteId).shouldBeInstanceOf<AppResult.Success<*>>()

            isAtendimentoFinished(otherClienteId) shouldBeEqualTo false
        }

    // region delete

    @Test
    fun deveVoltarParaHospedeAntigoQuandoAtendimentoEhExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val clienteId = db.saveFakeCliente()
            db.insertFakeEstadia(clienteId, db.saveFakeImovelId(), StayStatus.CheckOutDone)
            val atendimentoId = db.saveFakeAtendimento(clienteId)
            isLead(clienteId) shouldBeEqualTo true

            sut.delete(atendimentoId).shouldBeInstanceOf<AppResult.Success<*>>()

            isLead(clienteId) shouldBeEqualTo false
            db.atendimentoRegistrationReadDataSource().getAtendimento(atendimentoId)
                .shouldBeInstanceOf<AppResult.Error>()
        }

    // endregion

    private suspend fun isAtendimentoFinished(clienteId: Long): Boolean =
        (db.clienteDetailsReadDataSource().watchCliente(clienteId).first() as AppResult.Success)
            .data!!.atendimentos.single().isFinished

    private suspend fun isLead(clienteId: Long): Boolean? =
        (db.clienteDetailsReadDataSource().watchCliente(clienteId).first() as AppResult.Success)
            .data?.isLead
}
