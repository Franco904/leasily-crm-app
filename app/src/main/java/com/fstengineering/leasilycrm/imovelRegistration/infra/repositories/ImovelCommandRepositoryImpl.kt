package com.fstengineering.leasilycrm.imovelRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelCharacteristicCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelPriceDatabaseDataSource
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelCommandRepository
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.infra.mappers.toLocalDataModel

class ImovelCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
    private val imovelCharacteristicCrossRefDatabaseDataSource: ImovelCharacteristicCrossRefDatabaseDataSource,
    private val imovelPriceDatabaseDataSource: ImovelPriceDatabaseDataSource,
    private val imovelFileDatabaseDataSource: ImovelFileDatabaseDataSource,
) : ImovelCommandRepository {
    override suspend fun insertOrUpdate(imovel: Imovel): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            saveDataInDatabase(imovel)
        }
    }

    private suspend fun saveDataInDatabase(imovel: Imovel) {
        val imovelLocalModel = imovel.toLocalDataModel()

        val imovelId = imovelDatabaseDataSource.insertOrUpdate(imovelLocalModel)
            .let { imovel.id ?: it }

        imovel.characteristics.forEach {
            val characteristic = ImovelCharacteristicCrossRefOrm(
                imovelCharacteristicId = it.id,
                imovelId = imovelId,
            )
            imovelCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        imovel.prices.forEach {
            val priceLocalModel = it.toLocalDataModel(imovelId)
            imovelPriceDatabaseDataSource.insertOrUpdate(priceLocalModel)
        }

        imovel.files.forEach {
            val fileLocalModel = it.toLocalDataModel(imovelId)
            imovelFileDatabaseDataSource.insertOrUpdate(fileLocalModel)
        }
    }
}
