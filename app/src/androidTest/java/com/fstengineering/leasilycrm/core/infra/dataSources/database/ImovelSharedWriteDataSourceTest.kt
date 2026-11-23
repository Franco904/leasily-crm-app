package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
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
            val imovelId = db.saveFakeImovelId()
            val outroImovelId = db.saveFakeImovelId()

            val result = sut.delete(imovelId)

            result.shouldBeInstanceOf<AppResult.Success<*>>()
            imovelDao.getAll().map { it.imovel.id }.shouldBeEqualTo(listOf(outroImovelId))
        }

    // endregion
}
