package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoSelectableImovelReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelReadDataSource

    @Before
    fun setUp() {
        sut = db.atendimentoRegistrationImovelReadDataSource()
    }

    @Test
    fun deveListarImoveisEmOrdemAlfabeticaDeBairroQuandoNaoHaFiltroEHaImovelExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            saveImovel(id = 1L, neighborhood = "Centro")
            saveImovel(id = 2L, neighborhood = "Trindade")
            saveImovel(id = 3L, neighborhood = "Campeche")
            db.imovelSharedWriteDataSource().delete(imovelId = 3L)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            val result = sut.getImoveis()

            result.shouldBeInstanceOf<AppResult.Success<List<AtendimentoSelectableImovelReadModel>>>()
                .data.map { it.id } shouldBeEqualTo listOf(1L, 2L)
        }

    @Test
    fun deveFiltrarImoveisPorBairroOuEnderecoQuandoFiltroTemCaixaDiferente(): Unit =
        runBlocking(Dispatchers.Default) {
            saveImovel(id = 1L, neighborhood = "Centro", streetAddress = "Rua Trindade")
            saveImovel(id = 2L, neighborhood = "Trindade", streetAddress = "Rua A")
            saveImovel(id = 3L, neighborhood = "Campeche", streetAddress = "Rua B")

            val result = sut.getImoveis(matching = "trindade")

            result.shouldBeInstanceOf<AppResult.Success<List<AtendimentoSelectableImovelReadModel>>>()
                .data.map { it.id } shouldBeEqualTo listOf(1L, 2L)
        }

    @Test
    fun deveTratarPorcentagemComoTextoLiteralQuandoFiltroContemPorcentagem(): Unit =
        runBlocking(Dispatchers.Default) {
            saveImovel(id = 1L, neighborhood = "Centro")

            val result = sut.getImoveis(matching = "%")

            result.shouldBeInstanceOf<AppResult.Success<List<AtendimentoSelectableImovelReadModel>>>()
                .data shouldBeEqualTo emptyList()
        }

    private suspend fun saveImovel(
        id: Long,
        neighborhood: String,
        streetAddress: String = "Rua Qualquer",
    ) {
        val imovel = createFakeImovel(
            proprietarioId = db.saveFakeProprietario(proprietarioId = id),
            id = id,
            neighborhood = neighborhood,
            streetAddress = streetAddress,
        )
        db.imovelWriteDataSource().save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()
    }
}
