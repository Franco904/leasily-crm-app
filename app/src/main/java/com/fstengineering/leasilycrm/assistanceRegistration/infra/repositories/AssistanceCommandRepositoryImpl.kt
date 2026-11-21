package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers.toLocalDataModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentImovelCrossRefDatabaseDataSource

class AssistanceCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
    private val assistanceCharacteristicCrossRefDatabaseDataSource: AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource,
    private val assistanceInteractionDatabaseDataSource: AssistanceInteractionDatabaseDataSource,
    private val assistanceSearchedNeighborhoodDatabaseDataSource: AssistanceSearchedNeighborhoodDatabaseDataSource,
    private val assistanceSentImovelCrossRefDatabaseDataSource: AssistanceSentImovelCrossRefDatabaseDataSource,
) : AssistanceCommandRepository {
    override suspend fun insertOrUpdate(assistance: Assistance): AppResult<Unit> {
        return databaseAdapter.transaction {
            saveDataInDatabase(assistance)
        }
    }

    private suspend fun saveDataInDatabase(assistance: Assistance) {
        val assistanceEntity = assistance.toLocalDataModel()

        val assistanceId = assistanceDatabaseDataSource.insertOrUpdate(assistanceEntity)
            .let { id -> assistanceEntity.id ?: id }

        assistance.preferredImovelCharacteristics.forEach {
            val characteristic = AssistanceImovelCharacteristicCrossRefEntity(
                imovelCharacteristicId = it.id,
                assistanceId = assistanceId,
            )

            assistanceCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        assistance.interactions.forEach {
            val interaction = it.toLocalDataModel(assistanceId)
            assistanceInteractionDatabaseDataSource.insertOrUpdate(interaction)
        }

        assistance.preferredNeighborhoods.forEach {
            val searchedNeighborhood = AssistancePreferredNeighborhoodEntity(
                neighborhood = it,
                assistanceId = assistanceId,
            )

            assistanceSearchedNeighborhoodDatabaseDataSource.insertOrUpdate(searchedNeighborhood)
        }

        assistance.sentImoveisIds.forEach {
            val sentImovel = AssistanceSentImovelCrossRefEntity(
                assistanceId = assistanceId,
                imovelId = it,
            )

            assistanceSentImovelCrossRefDatabaseDataSource.insertOrUpdate(sentImovel)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit> {
        return databaseAdapter.transaction {
            assistanceDatabaseDataSource.delete(id)
        }
    }
}
