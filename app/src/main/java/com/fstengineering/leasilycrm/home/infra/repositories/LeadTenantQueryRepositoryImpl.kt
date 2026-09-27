package com.fstengineering.leasilycrm.home.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.home.application.models.dtos.LeadTenantListItemReadModel
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantQueryRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LeadTenantQueryRepositoryImpl(
    private val tenantDatabaseDataSource: TenantDatabaseDataSource,
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : LeadTenantQueryRepository {

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<AppResult<ImmutableList<LeadTenantListItemReadModel>, ApplicationError>> = flow {
        val tenantsFlow = tenantDatabaseDataSource.watchAll(
            tenantStatusIdFilter = tenantStatusFilter?.id,
            matching = matching,
            orderTypeId = orderTypeId,
        )
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it },
            )
            .map { views ->
                val readModels = views.map { view -> view.toReadModel() }.toPersistentList()
                AppResult.Success<ImmutableList<LeadTenantListItemReadModel>, ApplicationError>(data = readModels)
            }
            .distinctUntilChanged()

        emitAll(tenantsFlow)
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<LeadTenantListItemReadModel>, ApplicationError> {
        val views = tenantDatabaseDataSource.getAllLeads(
            matching = matching,
            order = orderTypeId,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )

        val readModels = views.map { view -> view.toReadModel() }.toPersistentList()
        return AppResult.Success(data = readModels)
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, ApplicationError> {
        val orderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.leadsOrderTypeId ?: 1 },
            )

        return AppResult.Success(data = orderTypeId)
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, ApplicationError> {
        val orderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.tenantsOrderTypeId ?: 1 },
            )

        return AppResult.Success(data = orderTypeId)
    }

    override suspend fun updatePreferredLeadsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, ApplicationError> {
        return plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(leadsOrderTypeId = orderTypeId)
        }
    }

    override suspend fun updatePreferredTenantsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, ApplicationError> {
        return plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(tenantsOrderTypeId = orderTypeId)
        }
    }

    private suspend fun LeadTenantReadView.toReadModel(): LeadTenantListItemReadModel {
        val currentStayId = if (hasUnfinishedStay) {
            stayDatabaseDataSource.getCurrentStayIdByTenantId(id!!)
                .fold(onError = { null }, onSuccess = { it })
        } else null

        val guestsCount = if (hasUnfinishedAssistance) {
            assistanceDatabaseDataSource.getCurrentAssistanceGuestsCountByTenantId(id!!)
                .fold(onError = { null }, onSuccess = { it })
        } else null

        return LeadTenantListItemReadModel(
            id = id!!,
            name = name,
            city = city,
            hasUnfinishedAssistance = hasUnfinishedAssistance,
            guestsCount = guestsCount,
            hasUnfinishedStay = hasUnfinishedStay,
            currentStayId = currentStayId,
        )
    }
}
