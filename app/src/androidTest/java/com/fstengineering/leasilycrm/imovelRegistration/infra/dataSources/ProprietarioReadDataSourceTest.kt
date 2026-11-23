package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
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
class ProprietarioReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ProprietarioReadDataSource

    @Before
    fun setUp() {
        sut = db.imovelRegistrationProprietarioReadDataSource()
    }

    // region watchProprietarios

    @Test
    fun deveListarProprietariosSelecionaveisEmOrdemAlfabeticaSemOsExcluidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val carla = ProprietarioReadModel(id = 1L, name = "Carla")
            val ana = ProprietarioReadModel(id = 2L, name = "Ana")
            val bruno = ProprietarioReadModel(id = 3L, name = "Bruno")
            listOf(carla, ana, bruno).forEach { saveProprietario(it) }
            db.proprietarioWriteDataSource().delete(bruno.id)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            val result = sut.watchProprietarios().first()

            result.shouldBeInstanceOf<AppResult.Success<*>>()
                .data shouldBeEqualTo listOf(ana, carla)
        }

    // endregion

    private suspend fun saveProprietario(readModel: ProprietarioReadModel) {
        db.saveFakeProprietario(name = readModel.name, proprietarioId = readModel.id)
    }
}
