package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource

class AssistanceCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
    private val assistanceCharacteristicCrossRefDatabaseDataSource: AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource,
    private val assistanceInteractionDatabaseDataSource: AssistanceInteractionDatabaseDataSource,
    private val assistanceSearchedNeighborhoodDatabaseDataSource: AssistanceSearchedNeighborhoodDatabaseDataSource,
    private val assistanceSentPropertyCrossRefDatabaseDataSource: AssistanceSentPropertyCrossRefDatabaseDataSource,
) : AssistanceCommandRepository {
    override suspend fun insertOrUpdate(assistance: Assistance): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(assistance)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private suspend fun saveDataInDatabase(assistance: Assistance) {
        val assistanceEntity = AssistanceEntity.fromDomain(assistance)

        val assistanceId = assistanceDatabaseDataSource.insertOrUpdate(assistanceEntity)
            .let { id -> assistanceEntity.id ?: id }

        assistance.preferredPropertyCharacteristics.forEach {
            val characteristic = AssistancePropertyCharacteristicCrossRefEntity(
                propertyCharacteristicId = it.id,
                assistanceId = assistanceId,
            )

            assistanceCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        assistance.interactions.forEach {
            val interaction = AssistanceInteractionEntity.fromEntity(it, assistanceId)
            assistanceInteractionDatabaseDataSource.insertOrUpdate(interaction)
        }

        assistance.preferredNeighborhoods.forEach {
            val searchedNeighborhood = AssistancePreferredNeighborhoodEntity(
                neighborhood = it,
                assistanceId = assistanceId,
            )

            assistanceSearchedNeighborhoodDatabaseDataSource.insertOrUpdate(searchedNeighborhood)
        }

        assistance.sentPropertiesIds.forEach {
            val sentProperty = AssistanceSentPropertyCrossRefEntity.fromDomain(it, assistanceId)
            assistanceSentPropertyCrossRefDatabaseDataSource.insertOrUpdate(sentProperty)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            assistanceDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }
}
