package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentTypeDatabaseDataSource
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
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: DomainTablesRemoteDataSource,
    private val assistanceCharacteristicDataSource: AssistanceCharacteristicDatabaseDataSource,
    private val customerSegmentTypeDataSource: CustomerSegmentTypeDatabaseDataSource,
    private val propertyCharacteristicDataSource: PropertyCharacteristicDatabaseDataSource,
    private val propertySeaProximityTypeDataSource: PropertySeaProximityTypeDatabaseDataSource,
    private val propertyTypeDataSource: PropertyTypeDatabaseDataSource,
    private val tenantContactOriginTypeDataSource: TenantContactOriginTypeDatabaseDataSource,
) : DomainTablesRepository {
    override suspend fun getDomainTables(): AppResult<Unit, DataError> = coroutineScope {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return@coroutineScope AppResult.Error(error = DataError.NoInternetError)
        }

        val domainTablesDto = remoteDataSource.getDomainTables()
            .fold(
                onError = { return@coroutineScope AppResult.Error(error = it) },
                onSuccess = { domainTablesDto -> domainTablesDto }
            )

        listOf(
            saveAssistanceCharacteristics(domainTablesDto),
            saveCustomerSegmentTypes(domainTablesDto),
            savePropertyCharacteristics(domainTablesDto),
            savePropertySeaProximityTypes(domainTablesDto),
            savePropertyTypes(domainTablesDto),
            saveTenantContactOriginTypes(domainTablesDto),
        )
            .awaitAll()

        AppResult.Success(data = Unit)
    }

    private fun CoroutineScope.saveAssistanceCharacteristics(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.assistanceCharacteristics.forEach {
            val entity = AssistanceCharacteristicEntity.fromDomain(it.toDomain())
            assistanceCharacteristicDataSource.insertOrUpdate(entity)
        }
    }

    private fun CoroutineScope.saveCustomerSegmentTypes(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.customerSegmentTypes.forEach {
            val entity = CustomerSegmentTypeEntity.fromDomain(it.toDomain())
            customerSegmentTypeDataSource.insertOrUpdate(entity)
        }
    }

    private fun CoroutineScope.savePropertyCharacteristics(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.propertyCharacteristics.forEach {
            val entity = PropertyCharacteristicEntity.fromDomain(it.toDomain())
            propertyCharacteristicDataSource.insertOrUpdate(entity)
        }
    }

    private fun CoroutineScope.savePropertySeaProximityTypes(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.propertySeaProximityTypes.forEach {
            val entity = PropertySeaProximityTypeEntity.fromDomain(it.toDomain())
            propertySeaProximityTypeDataSource.insertOrUpdate(entity)
        }
    }

    private fun CoroutineScope.savePropertyTypes(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.propertyTypes.forEach {
            val entity = PropertyTypeEntity.fromDomain(it.toDomain())
            propertyTypeDataSource.insertOrUpdate(entity)
        }
    }

    private fun CoroutineScope.saveTenantContactOriginTypes(
        domainTablesDto: GetDomainTablesDto,
    ) = async {
        domainTablesDto.tenantContactOriginTypes.forEach {
            val entity = TenantContactOriginTypeEntity.fromDomain(it.toDomain())
            tenantContactOriginTypeDataSource.insertOrUpdate(entity)
        }
    }
}
