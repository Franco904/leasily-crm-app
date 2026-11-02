package com.fstengineering.leasilycrm.proprietarios.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
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
class ProprietariosReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ProprietariosReadDataSource

    private lateinit var proprietarioWriteDataSource: ProprietarioWriteDataSource

    @Before
    fun setUp() {
        sut = db.proprietariosReadDataSource()
        proprietarioWriteDataSource = db.proprietarioWriteDataSource()
    }

    // region watchProprietarios

    @Test
    fun deveListarProprietariosEmOrdemAlfabeticaSemOsExcluidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val carla = ProprietarioReadModel(id = 1L, name = "Carla")
            val ana = ProprietarioReadModel(id = 2L, name = "Ana")
            val bruno = ProprietarioReadModel(id = 3L, name = "Bruno")
            listOf(carla, ana, bruno).forEach {
                proprietarioWriteDataSource.save(createFakeProprietario(id = it.id, name = it.name))
                    .shouldBeInstanceOf<AppResult.Success<*, *>>()
            }
            proprietarioWriteDataSource.delete(bruno.id)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.watchProprietarios().first()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data shouldBeEqualTo listOf(ana, carla)
        }

    // endregion

    private fun createFakeProprietario(id: Long, name: String): Proprietario = Proprietario.parse(
        id = id,
        createDateTime = null,
        name = name,
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf<ProprietarioFile>(),
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()
}
