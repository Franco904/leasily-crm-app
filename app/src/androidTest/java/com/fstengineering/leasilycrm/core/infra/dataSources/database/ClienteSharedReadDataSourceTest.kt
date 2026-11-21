package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ClienteSharedReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ClienteSharedReadDataSource

    @Before
    fun setUp() {
        sut = db.clienteSharedReadDataSource()
    }

    @Test
    fun deveObterONomeDoClienteQuandoClienteExiste(): Unit = runBlocking(Dispatchers.Default) {
        val clienteId = db.saveFakeCliente(name = "Maria Silva")

        val result = sut.getName(clienteId)

        result.shouldBeInstanceOf<AppResult.Success<*>>().data shouldBeEqualTo "Maria Silva"
    }

    @Test
    fun deveFalharQuandoClienteFoiExcluido(): Unit = runBlocking(Dispatchers.Default) {
        val clienteId = db.saveFakeCliente()
        db.clienteWriteDataSource().delete(clienteId)

        val result = sut.getName(clienteId)

        result.shouldBeInstanceOf<AppResult.Error>()
    }
}
