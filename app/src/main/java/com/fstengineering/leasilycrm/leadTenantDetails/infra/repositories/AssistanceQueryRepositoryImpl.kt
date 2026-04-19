package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toSentPropertyReadModel
import kotlinx.collections.immutable.toPersistentList

class AssistanceQueryRepositoryImpl(
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : AssistanceQueryRepository {
    override suspend fun getAllByLeadTenantId(
        leadTenantId: Long,
    ): AppResult<List<AssistanceReadModel>, ApplicationError> {
        return assistanceDatabaseDataSource.getAllByLeadTenantId(leadTenantId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { compounds ->
                    val assistances = compounds.map { compound ->
                        val sentProperties = propertyDatabaseDataSource
                            .getAllSentInAssistance(requireNotNull(compound.assistance.id))
                            .fold(
                                onError = { emptyList() },
                                onSuccess = { it.map { p -> p.toSentPropertyReadModel() } },
                            )
                            .toPersistentList()

                        compound.toReadModel(sentProperties = sentProperties)
                    }

                    AppResult.Success(data = assistances)
                },
            )
    }
}
