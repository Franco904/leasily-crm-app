package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
    private val assistanceCharacteristicCrossRefDatabaseDataSource: AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource,
    private val assistanceInteractionDatabaseDataSource: AssistanceInteractionDatabaseDataSource,
    private val assistanceSearchedNeighborhoodDatabaseDataSource: AssistanceSearchedNeighborhoodDatabaseDataSource,
    private val assistanceSentPropertyCrossRefDatabaseDataSource: AssistanceSentPropertyCrossRefDatabaseDataSource,
) : AssistanceRepository {
    override suspend fun insertOrUpdate(assistance: Assistance): AppResult<Unit, DataError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(assistance)
        }
    }

    private suspend fun saveDataInDatabase(assistance: Assistance) {
        val assistanceEntity = AssistanceEntity.fromDomain(assistance)

        val assistanceId = assistanceDatabaseDataSource.insertOrUpdate(assistanceEntity)
            .let { id -> assistanceEntity.id ?: id }

        assistance.characteristics.forEach {
            val characteristic =
                AssistancePropertyCharacteristicCrossRefEntity.fromDomain(it, assistanceId)
            assistanceCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        assistance.interactions.forEach {
            val interaction = AssistanceInteractionEntity.fromDomain(it, assistanceId)
            assistanceInteractionDatabaseDataSource.insertOrUpdate(interaction)
        }

        assistance.searchedNeighborhoods.forEach {
            val searchedNeighborhood =
                AssistanceSearchedNeighborhoodEntity.fromDomain(it, assistanceId)
            assistanceSearchedNeighborhoodDatabaseDataSource.insertOrUpdate(searchedNeighborhood)
        }

        assistance.sentProperties.forEach {
            val sentProperty = AssistanceSentPropertyCrossRefEntity.fromDomain(it, assistanceId)
            assistanceSentPropertyCrossRefDatabaseDataSource.insertOrUpdate(sentProperty)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        databaseAdapter.withTransaction {
            assistanceDatabaseDataSource.delete(id)
        }

    override suspend fun getById(assistanceId: Long): AppResult<Assistance, DataError> {
        val assistance = assistanceDatabaseDataSource.getById(id = assistanceId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { assistance -> assistance.toDomain() }
            )

        return AppResult.Success(data = assistance)
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataError> {
        val neighborhoodNames =
            assistanceDatabaseDataSource.getAllNeighborhoodNames()
                .fold(
                    onError = { return AppResult.Error(error = it) },
                    onSuccess = { neighborhoodNames -> neighborhoodNames.toPersistentList() }
                )

        return AppResult.Success(data = neighborhoodNames)
    }
}
