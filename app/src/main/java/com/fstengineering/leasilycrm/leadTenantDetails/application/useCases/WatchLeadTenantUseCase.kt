package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries.LeadTenantArgs
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.StayQueryRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchLeadTenantUseCase(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val assistanceSentPropertyQueryRepository: AssistanceSentPropertyQueryRepository,
    private val stayQueryRepository: StayQueryRepository,
) {
    suspend operator fun invoke(args: LeadTenantArgs): Flow<AppResult<LeadTenantReadModel?, ApplicationError>> {
        return leadTenantQueryRepository.watchById(args.tenantId).map outer@{ tenantResult ->
            val raw = tenantResult.fold(
                onError = { e -> return@outer AppResult.Error(error = e) },
                onSuccess = { it },
            ) ?: return@outer AppResult.Success(data = null)

            val assistances = loadAssistances(leadTenantId = raw.id)
                .fold(
                    onError = { e -> return@outer AppResult.Error(error = e) },
                    onSuccess = { storedAssistances -> storedAssistances },
                )
                .map { assistanceRaw ->
                    val sentProperties = loadSentProperties(assistanceRaw.id)
                        .fold(
                            onError = { e -> return@outer AppResult.Error(error = e) },
                            onSuccess = { storedSentProperties -> storedSentProperties },
                        )

                    AssistanceReadModel.create(
                        assistanceRaw = assistanceRaw,
                        sentProperties = sentProperties,
                    )
                }
                .toPersistentList()

            val stays = stayQueryRepository.getAllByTenantId(raw.id)
                .fold(
                    onError = { e -> return@outer AppResult.Error(error = e) },
                    onSuccess = { it },
                )
                .toPersistentList()

            val readModel = LeadTenantReadModel(
                id = raw.id,
                tag = raw.tag,
                name = raw.name,
                city = raw.city,
                identityDocument = raw.identityDocument,
                phone = raw.phone,
                email = raw.email,
                contactOrigin = raw.contactOrigin,
                observations = raw.observations,
                isLead = raw.status == 0,
                isOld = raw.status == 2,
                assistances = assistances,
                stays = stays,
            )

            AppResult.Success(data = readModel)
        }
    }

    private suspend fun loadAssistances(
        leadTenantId: Long?,
    ): AppResult<ImmutableList<AssistanceRawReadModel>, ApplicationError> {
        if (leadTenantId == null) {
            return AppResult.Success(persistentListOf())
        }

        return assistanceQueryRepository.getAllByLeadTenantId(leadTenantId)
    }

    private suspend fun loadSentProperties(
        assistanceId: Long?,
    ): AppResult<ImmutableList<AssistanceSentPropertyReadModel>, ApplicationError> {
        if (assistanceId == null) {
            return AppResult.Success(persistentListOf())
        }

        return assistanceSentPropertyQueryRepository.getAllSentInAssistance(assistanceId)
    }
}
