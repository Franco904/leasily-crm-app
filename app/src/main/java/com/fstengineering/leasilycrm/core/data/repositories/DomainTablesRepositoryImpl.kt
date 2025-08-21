package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertySeaProximityTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.DomainTablesApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.domainTables.GetDomainTablesDto
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DomainTablesRepositoryImpl(
    private val api: DomainTablesApi,
    private val assistanceCharacteristicDao: AssistanceCharacteristicDao,
    private val customerSegmentTypeDao: CustomerSegmentTypeDao,
    private val propertyCharacteristicDao: PropertyCharacteristicDao,
    private val propertySeaProximityTypeDao: PropertySeaProximityTypeDao,
    private val propertyTypeDao: PropertyTypeDao,
    private val tenantContactOriginTypeDao: TenantContactOriginTypeDao,
) : DomainTablesRepository {
    override suspend fun getDomainTables(): AppResult<Unit, DataSourceError> = coroutineScope {
        val domainTablesDto = api.getDomainTables()
            .fold(
                onError = { return@coroutineScope AppResult.Error(error = it) },
                onSuccess = { domainTablesDto -> domainTablesDto }
            )

        saveAssistanceCharacteristics(domainTablesDto)
        saveCustomerSegmentTypes(domainTablesDto)
        savePropertyCharacteristics(domainTablesDto)
        savePropertySeaProximityTypes(domainTablesDto)
        savePropertyTypes(domainTablesDto)
        saveTenantContactOriginTypes(domainTablesDto)

        AppResult.Success(data = Unit)
    }

    private fun CoroutineScope.saveAssistanceCharacteristics(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.assistanceCharacteristics.forEach {
                val entity = AssistanceCharacteristicEntity.fromDomain(it.toDomain())
                assistanceCharacteristicDao.insertOrUpdate(entity)
            }
        }
    }

    private fun CoroutineScope.saveCustomerSegmentTypes(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.customerSegmentTypes.forEach {
                val entity = CustomerSegmentTypeEntity.fromDomain(it.toDomain())
                customerSegmentTypeDao.insertOrUpdate(entity)
            }
        }
    }

    private fun CoroutineScope.savePropertyCharacteristics(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.propertyCharacteristics.forEach {
                val entity = PropertyCharacteristicEntity.fromDomain(it.toDomain())
                propertyCharacteristicDao.insertOrUpdate(entity)
            }
        }
    }

    private fun CoroutineScope.savePropertySeaProximityTypes(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.propertySeaProximityTypes.forEach {
                val entity = PropertySeaProximityTypeEntity.fromDomain(it.toDomain())
                propertySeaProximityTypeDao.insertOrUpdate(entity)
            }
        }
    }

    private fun CoroutineScope.savePropertyTypes(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.propertyTypes.forEach {
                val entity = PropertyTypeEntity.fromDomain(it.toDomain())
                propertyTypeDao.insertOrUpdate(entity)
            }
        }
    }

    private fun CoroutineScope.saveTenantContactOriginTypes(
        domainTablesDto: GetDomainTablesDto,
    ) {
        launch {
            domainTablesDto.tenantContactOriginTypes.forEach {
                val entity = TenantContactOriginTypeEntity.fromDomain(it.toDomain())
                tenantContactOriginTypeDao.insertOrUpdate(entity)
            }
        }
    }
}
