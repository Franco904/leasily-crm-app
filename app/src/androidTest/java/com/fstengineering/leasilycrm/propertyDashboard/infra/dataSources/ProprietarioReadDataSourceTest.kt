package com.fstengineering.leasilycrm.propertyDashboard.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
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

    private lateinit var proprietarioWriteDataSource: ProprietarioWriteDataSource

    @Before
    fun setUp() {
        sut = db.propertyDashboardProprietarioReadDataSource()
        proprietarioWriteDataSource = db.proprietarioWriteDataSource()
    }

    // region getProprietario

    @Test
    fun deveBuscarProprietarioDoImovelQuandoCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val ana = ProprietarioReadModel(id = 1L, name = "Ana")
            saveProprietario(ana)

            val result = sut.getProprietario(ana.id)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>().data shouldBeEqualTo ana
        }

    @Test
    fun deveFalharAoBuscarProprietarioDoImovelQuandoExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val ana = ProprietarioReadModel(id = 1L, name = "Ana")
            saveProprietario(ana)
            proprietarioWriteDataSource.delete(ana.id)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getProprietario(ana.id)

            result.shouldBeInstanceOf<AppResult.Error<*, *>>().error shouldBeEqualTo
                ApplicationError.Unknown
        }

    // endregion

    private suspend fun saveProprietario(readModel: ProprietarioReadModel) {
        val proprietario = Proprietario.parse(
            id = readModel.id,
            createDateTime = null,
            name = readModel.name,
            phone = null,
            email = null,
            paymentKey = null,
            files = persistentListOf<ProprietarioFile>(),
        ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()

        proprietarioWriteDataSource.save(proprietario)
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
    }
}
