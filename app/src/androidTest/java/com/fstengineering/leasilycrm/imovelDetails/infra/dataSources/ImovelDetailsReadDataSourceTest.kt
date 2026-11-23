package com.fstengineering.leasilycrm.imovelDetails.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelFileReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelPriceReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovelFile
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovelPrice
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelDetailsReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelDetailsReadDataSource

    private lateinit var imovelWriteDataSource: ImovelWriteDataSource

    @Before
    fun setUp() {
        sut = db.imovelDetailsReadDataSource()
        imovelWriteDataSource = db.imovelWriteDataSource()
    }

    // region watchImovel

    @Test
    fun deveObservarImovelComCaracteristicasPrecosEArquivosQuandoImovelEstaCadastrado(): Unit =
        runBlocking(Dispatchers.Default) {
            val characteristics = ImovelCharacteristic.entries.take(2)
            val price = createFakeImovelPrice()
            val file =
                createFakeImovelFile(id = null, uriString = faker.random.nextUUID(), isMainPicture = true)
            val imovel = createFakeImovel(
                proprietarioId = db.saveFakeProprietario(),
                id = 7L,
                characteristics = characteristics,
                prices = listOf(price),
                files = listOf(file),
            )
            imovelWriteDataSource.save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()

            val result = sut.watchImovel(imovelId = 7L).first()

            val readModel = result
                .shouldBeInstanceOf<AppResult.Success<ImovelReadModel>>().data
            readModel.id shouldBeEqualTo 7L
            readModel.type shouldBeEqualTo ImovelType.Apartment
            readModel.neighborhood shouldBeEqualTo imovel.neighborhood
            readModel.streetAddress shouldBeEqualTo imovel.streetAddress
            readModel.characteristics shouldContainSame characteristics
            readModel.prices shouldBeEqualTo listOf(
                ImovelPriceReadModel(
                    startDate = price.period.startDate,
                    endDate = price.period.endDate,
                    amount = price.amount.value,
                ),
            )
            readModel.files shouldBeEqualTo listOf(
                ImovelFileReadModel(uriString = file.uriString, isMainPicture = true),
            )
        }

    @Test
    fun deveNaoObservarImovelQuandoImovelEhExcluido(): Unit = runBlocking(Dispatchers.Default) {
        val imovel = createFakeImovel(proprietarioId = db.saveFakeProprietario(), id = 8L)
        imovelWriteDataSource.save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()
        db.imovelSharedWriteDataSource().delete(imovelId = 8L)
            .shouldBeInstanceOf<AppResult.Success<*>>()

        val result = withTimeoutOrNull(500) { sut.watchImovel(imovelId = 8L).first() }

        result.shouldBeNull()
    }

    // endregion
}
