package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.data.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertySeaProximityTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.TenantContactOriginTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.remote.interfaces.DomainTablesRemoteDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.remote.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DomainTablesRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceCharacteristicDataSource: AssistanceCharacteristicDatabaseDataSource,
    private val propertyCharacteristicDataSource: PropertyCharacteristicDatabaseDataSource,
    private val propertySeaProximityTypeDataSource: PropertySeaProximityTypeDatabaseDataSource,
    private val propertyTypeDataSource: PropertyTypeDatabaseDataSource,
    private val tenantContactOriginTypeDataSource: TenantContactOriginTypeDatabaseDataSource,
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: DomainTablesRemoteDataSource,
) : DomainTablesRepository {
    override suspend fun getDomainTables(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        val domainTablesDto = remoteDataSource.getDomainTables()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { domainTablesDto -> domainTablesDto }
            )

        return databaseAdapter.withTransaction {
            saveDataInDatabase(domainTablesDto)
        }
    }

    private suspend fun saveDataInDatabase(domainTablesDto: GetDomainTablesDto) {
        domainTablesDto.assistanceCharacteristics.forEach {
            val entity = AssistanceCharacteristicEntity.fromDomain(it.toDomain())
            assistanceCharacteristicDataSource.insertOrUpdate(entity)
        }

        domainTablesDto.propertyCharacteristics.forEach {
            val entity = PropertyCharacteristicEntity.fromDomain(it.toDomain())
            propertyCharacteristicDataSource.insertOrUpdate(entity)
        }

        domainTablesDto.propertySeaProximityTypes.forEach {
            val entity = PropertySeaProximityTypeEntity.fromDomain(it.toDomain())
            propertySeaProximityTypeDataSource.insertOrUpdate(entity)
        }

        domainTablesDto.propertyTypes.forEach {
            val entity = PropertyTypeEntity.fromDomain(it.toDomain())
            propertyTypeDataSource.insertOrUpdate(entity)
        }

        domainTablesDto.tenantContactOriginTypes.forEach {
            val entity = TenantContactOriginTypeEntity.fromDomain(it.toDomain())
            tenantContactOriginTypeDataSource.insertOrUpdate(entity)
        }
    }
}
