package com.fstengineering.leasilycrm.propertyRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyPriceDatabaseDataSource
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyCommandRepository
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.infra.mappers.toLocalDataModel

class PropertyCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
    private val propertyCharacteristicCrossRefDatabaseDataSource: PropertyCharacteristicCrossRefDatabaseDataSource,
    private val propertyPriceDatabaseDataSource: PropertyPriceDatabaseDataSource,
    private val propertyFileDatabaseDataSource: PropertyFileDatabaseDataSource,
) : PropertyCommandRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            saveDataInDatabase(property)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private suspend fun saveDataInDatabase(property: Property) {
        val propertyLocalModel = property.toLocalDataModel()

        val propertyId = propertyDatabaseDataSource.insertOrUpdate(propertyLocalModel)
            .let { property.id ?: it }

        property.characteristics.forEach {
            val characteristic = PropertyCharacteristicCrossRefEntity(
                propertyCharacteristicId = it.id,
                propertyId = propertyId,
            )
            propertyCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        property.prices.forEach {
            val priceLocalModel = it.toLocalDataModel(propertyId)
            propertyPriceDatabaseDataSource.insertOrUpdate(priceLocalModel)
        }

        property.files.forEach {
            val fileLocalModel = it.toLocalDataModel(propertyId)
            propertyFileDatabaseDataSource.insertOrUpdate(fileLocalModel)
        }
    }
}
