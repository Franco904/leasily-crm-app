package com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources.ProprietarioDetailsReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProprietarioWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: ProprietarioWriteDataSource

    private lateinit var proprietarioDetailsReadDataSource: ProprietarioDetailsReadDataSource

    @Before
    fun setUp() {
        sut = db.proprietarioWriteDataSource()
        proprietarioDetailsReadDataSource = db.proprietarioDetailsReadDataSource()
    }

    // region save

    @Test
    fun deveExibirProprietarioNosDetalhesComSeusArquivosQuandoCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
            val files = persistentListOf(createFakeProprietarioFile(), createFakeProprietarioFile())
            val proprietario = createFakeProprietario(id = proprietarioId, files = files)

            val result = sut.save(proprietario)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            val proprietarioReadModel = proprietarioDetailsReadDataSource
                .watchProprietario(proprietarioId)
                .first()
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldNotBeNull().shouldBeInstanceOf<ProprietarioReadModel>()
            proprietarioReadModel.copy(files = persistentListOf()) shouldBeEqualTo ProprietarioReadModel(
                name = proprietario.name.value,
                phone = proprietario.phone?.value,
                email = proprietario.email?.value,
                paymentKey = proprietario.paymentKey?.value,
                files = persistentListOf(),
            )
            proprietarioReadModel.files.toSet() shouldBeEqualTo files
                .map { ProprietarioFileReadModel(uriString = it.uriString, name = it.name) }
                .toSet()
        }

    @Test
    fun deveExibirDadosAtualizadosNosDetalhesSemDuplicarArquivosQuandoProprietarioEditado(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
            val existingFile = createFakeProprietarioFile(
                id = faker.random.nextInt(bound = 9999).toLong() + 1,
            )
            sut.save(createFakeProprietario(id = proprietarioId, files = listOf(existingFile)))
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
            val newFile = createFakeProprietarioFile()
            val editedProprietario = createFakeProprietario(
                id = proprietarioId,
                name = faker.random.nextUUID(),
                files = listOf(existingFile, newFile),
            )

            val result = sut.save(editedProprietario)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            val proprietarioReadModel = proprietarioDetailsReadDataSource
                .watchProprietario(proprietarioId)
                .first()
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldNotBeNull().shouldBeInstanceOf<ProprietarioReadModel>()
            proprietarioReadModel.name shouldBeEqualTo editedProprietario.name.value
            proprietarioReadModel.files.size shouldBeEqualTo 2
            proprietarioReadModel.files.toSet() shouldBeEqualTo listOf(existingFile, newFile)
                .map { ProprietarioFileReadModel(uriString = it.uriString, name = it.name) }
                .toSet()
        }

    // endregion

    // region delete

    @Test
    fun deveDeixarDeExibirProprietarioNosDetalhesQuandoExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
            sut.save(createFakeProprietario(id = proprietarioId, files = persistentListOf()))
                .shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.delete(proprietarioId)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            proprietarioDetailsReadDataSource
                .watchProprietario(proprietarioId)
                .first()
                .shouldBeInstanceOf<AppResult.Success<*, *>>()
                .data.shouldBeNull()
        }

    // endregion

    private fun createFakeProprietario(
        id: Long,
        files: List<ProprietarioFile>,
        name: String = faker.random.nextUUID(),
    ): Proprietario = Proprietario.parse(
        id = id,
        createDateTime = null,
        name = name,
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf<ProprietarioFile>().addAll(files),
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()

    private fun createFakeProprietarioFile(id: Long? = null): ProprietarioFile = ProprietarioFile(
        id = id,
        uriString = "content://${faker.random.nextUUID()}",
        name = faker.random.nextUUID(),
    )
}
