package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimal
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelSharedWriteDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelSharedWriteDataSource

    private lateinit var imovelDao: ImovelDao

    @Before
    fun setUp() {
        sut = db.imovelSharedWriteDataSource()
        imovelDao = db.imovelDao()
    }

    // region delete

    @Test
    fun deveRemoverImovelDaListagemQuandoExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = saveFakeProprietario()
            val imovelId = saveFakeImovel(proprietarioId)
            val outroImovelId = saveFakeImovel(proprietarioId)

            val result = sut.delete(imovelId)

            result.shouldBeInstanceOf<AppResult.Success<*, *>>()
            imovelDao.getAll().map { it.imovel.id }.shouldBeEqualTo(listOf(outroImovelId))
        }

    // endregion

    private suspend fun saveFakeProprietario(): Long {
        val proprietarioId = faker.random.nextInt(bound = 9999).toLong() + 1
        val proprietario = Proprietario.parse(
            id = proprietarioId,
            createDateTime = null,
            name = faker.random.nextUUID(),
            phone = null,
            email = null,
            paymentKey = null,
            files = persistentListOf(),
        ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Proprietario>()
        db.proprietarioWriteDataSource().save(proprietario)
            .shouldBeInstanceOf<AppResult.Success<*, *>>()
        return proprietarioId
    }

    private suspend fun saveFakeImovel(proprietarioId: Long): Long {
        val imovel = Imovel.parse(
            proprietarioId = proprietarioId,
            type = ImovelType.Apartment,
            neighborhood = faker.random.nextUUID(),
            streetAddress = faker.random.nextUUID(),
            prices = persistentListOf(createFakeImovelPrice()),
        ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<Imovel>()
        db.imovelWriteDataSource().save(imovel).shouldBeInstanceOf<AppResult.Success<*, *>>()
        return imovelDao.getAll().maxOf { it.imovel.id!! }
    }

    private fun createFakeImovelPrice(): ImovelPrice = ImovelPrice.parse(
        startDate = AppDate(LocalDate.of(2030, 1, 1)),
        endDate = AppDate(LocalDate.of(2030, 1, 31)),
        amount = (faker.random.nextInt(bound = 9999) + 1).toDecimal(),
    ).shouldBeInstanceOf<AppResult.Success<*, *>>().data.shouldBeInstanceOf<ImovelPrice>()
}
