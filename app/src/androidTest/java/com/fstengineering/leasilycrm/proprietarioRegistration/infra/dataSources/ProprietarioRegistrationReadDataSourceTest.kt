package com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProprietarioRegistrationReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ProprietarioRegistrationReadDataSource

    private lateinit var proprietarioWriteDataSource: ProprietarioWriteDataSource

    @Before
    fun setUp() {
        sut = db.proprietarioRegistrationReadDataSource()
        proprietarioWriteDataSource = db.proprietarioWriteDataSource()
    }

    // region getProprietario

    @Test
    fun deveCarregarProprietarioComSeusArquivosParaEdicaoQuandoCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
            val files = listOf(
                createFakeProprietarioFile(id = 1L),
                createFakeProprietarioFile(id = 2L),
            )
            val proprietario = createFakeProprietario(id = proprietarioId, files = files)
            proprietarioWriteDataSource.save(proprietario)
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.getProprietario(proprietarioId)

            val proprietarioReadModel = result
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldNotBeNull().shouldBeInstanceOf<ProprietarioReadModel>()
            proprietarioReadModel.createDateTime.shouldNotBeNull()
            proprietarioReadModel.updateDateTime.shouldNotBeNull()
            proprietarioReadModel.copy(
                createDateTime = null,
                updateDateTime = null,
                files = persistentListOf(),
            ) shouldBeEqualTo ProprietarioReadModel(
                id = proprietarioId,
                createDateTime = null,
                updateDateTime = null,
                name = proprietario.name.value,
                phone = proprietario.phone?.value,
                email = proprietario.email?.value,
                paymentKey = proprietario.paymentKey?.value,
                files = persistentListOf(),
            )
            proprietarioReadModel.files.toSet() shouldBeEqualTo files
                .map { ProprietarioFileReadModel(id = it.id, uriString = it.uriString, name = it.name) }
                .toSet()
        }

    // endregion

    private fun createFakeProprietario(
        id: Long,
        files: List<ProprietarioFile>,
    ): Proprietario = Proprietario.parse(
        id = id,
        createDateTime = null,
        name = faker.random.nextUUID(),
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf<ProprietarioFile>().addAll(files),
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()

    private fun createFakeProprietarioFile(id: Long): ProprietarioFile = ProprietarioFile(
        id = id,
        uriString = "content://${faker.random.nextUUID()}",
        name = faker.random.nextUUID(),
    )
}
