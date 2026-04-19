package com.fstengineering.leasilycrm.leadTenantDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.StayQueryRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchLeadTenantUseCase(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val assistanceQueryRepository: AssistanceQueryRepository,
    private val stayQueryRepository: StayQueryRepository,
) {
    operator fun invoke(tenantId: Long): Flow<AppResult<LeadTenantReadModel?, ApplicationError>> {
        return leadTenantQueryRepository.watchById(tenantId).map outer@{ tenantResult ->
            val raw = tenantResult.fold(
                onError = { e -> return@outer AppResult.Error(error = e) },
                onSuccess = { it },
            ) ?: return@outer AppResult.Success(data = null)

            val assistances = assistanceQueryRepository.getAllByLeadTenantId(raw.id)
                .fold(
                    onError = { e -> return@outer AppResult.Error(error = e) },
                    onSuccess = { it },
                )
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
}
