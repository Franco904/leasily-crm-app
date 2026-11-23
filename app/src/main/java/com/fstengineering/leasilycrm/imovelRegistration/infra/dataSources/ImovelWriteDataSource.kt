package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.infra.mappers.toLocalDataModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Escrita do cadastro de Imóvel. */
@Dao
abstract class ImovelWriteDataSource : KoinComponent {
    private val dateTimeProvider: AppDateTimeProvider by inject()

    /**
     * Salva o Imóvel com seus Preços numa única transação: se o salvamento de algum Preço falha,
     * o Imóvel não é salvo.
     */
    suspend fun save(imovel: Imovel): AppResult<Unit> =
        onDatabase { saveInTransaction(imovel) }

    @Transaction
    protected open suspend fun saveInTransaction(imovel: Imovel) {
        val imovelId = upsert(imovel.toLocalDataModel(now = dateTimeProvider.now()))
            .let { generatedId -> imovel.id ?: generatedId }

        imovel.characteristics.forEach {
            val crossRef =
                ImovelCharacteristicCrossRefOrm(imovelCharacteristicId = it.id, imovelId = imovelId)
            upsert(crossRef)
        }

        imovel.prices.forEach {
            upsert(imovelPrice = it.toLocalDataModel(imovelId))
        }

        imovel.files.forEach {
            upsert(imovelFile = it.toLocalDataModel(imovelId))
        }
    }

    @Upsert
    protected abstract suspend fun upsert(imovel: ImovelOrm): Long

    @Upsert
    protected abstract suspend fun upsert(crossRef: ImovelCharacteristicCrossRefOrm): Long

    @Upsert
    protected abstract suspend fun upsert(imovelPrice: ImovelPriceOrm): Long

    @Upsert
    protected abstract suspend fun upsert(imovelFile: ImovelFileOrm): Long
}
