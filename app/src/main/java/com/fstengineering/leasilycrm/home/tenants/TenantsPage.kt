package com.fstengineering.leasilycrm.home.tenants

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByMenuOption
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByTextButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.LeadTenantCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteTenantDialog
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel.UiEvents.LeadDeleteSuccess
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.tenants.composables.TenantFilterButtonGroup
import com.fstengineering.leasilycrm.home.tenants.composables.TenantsEmptySection
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TenantsPage(
    viewModel: TenantsViewModel,
    isSearchingTenant: Boolean,
    searchInput: String?,
    onRegisterTenant: (Long?) -> Unit,
    onViewTenantDetails: (Long) -> Unit,
    onRegisterAssistance: (Long, Long?) -> Unit,
    onDeleteAssistance: (Assistance) -> Unit,
    onNewStay: (Long) -> Unit,
    onViewStay: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val currentTenantFilter by viewModel.currentTenantFilterType.collectAsStateWithLifecycle()
    val orderType by viewModel.orderType.collectAsStateWithLifecycle()

    val tenants by viewModel.tenants.collectAsStateWithLifecycle()
    val tenantToDelete by viewModel.tenantToDelete.collectAsStateWithLifecycle()

    val listStatus by remember(isLoading, tenants) {
        val status = when {
            isLoading -> ListState.Loading
            tenants.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }

        mutableStateOf(status)
    }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is LeadDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_tenant_succeeded))
                }
            }
        }
    }

    LaunchedEffect(searchInput) {
        viewModel.onSearchInputChange(searchInput = searchInput)
    }

    LaunchedEffect(isSearchingTenant) {
        val filterType = if (isSearchingTenant) null else viewModel.tenantFilterTypeBeforeSearch
        viewModel.onTenantFilterTypeChange(tenantFilterType = filterType)
    }

    Column {
        if (!isSearchingTenant && currentTenantFilter != null) {
            Spacer(modifier = Modifier.height(16.dp))
            TenantFilterButtonGroup(
                selectedFilterType = currentTenantFilter!!,
                onFilterTypeSelect = viewModel::onTenantFilterTypeChange,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
        AnimatedContent(
            targetState = listStatus,
            modifier = modifier
                .fillMaxSize()
        ) { listStatus ->
            when (listStatus) {
                ListState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        AppCircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }

                ListState.Empty -> {
                    TenantsEmptySection(
                        isSearchingTenant = isSearchingTenant,
                        tenantFilterType = currentTenantFilter,
                    )
                }

                ListState.Idle -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        if (!isSearchingTenant) {
                            item {
                                OrderByTextButton(
                                    orderType = orderType,
                                    orderByMenuOptions = persistentListOf(
                                        OrderByMenuOption(
                                            text = stringResource(R.string.order_by_name_text),
                                            onClick = {
                                                viewModel.onOrderTypeChange(
                                                    LeadTenantOrderType.Name,
                                                )
                                            },
                                        ),
                                        OrderByMenuOption(
                                            text = stringResource(R.string.order_by_last_update_date_text),
                                            onClick = {
                                                viewModel.onOrderTypeChange(
                                                    LeadTenantOrderType.LastUpdateDate,
                                                )
                                            },
                                        ),
                                    ),
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                        itemsIndexed(
                            items = tenants,
                            key = { _, tenant -> tenant.id!! },
                            contentType = { _, entryType -> entryType },
                        ) { _, tenant ->
                            val unfinishedAssistance =
                                remember(tenant) { tenant.assistances.find { !it.isFinished } }
                            val unfinishedStay =
                                remember(tenant) { tenant.stays.find { !it.isCheckOutDone } }

                            LeadTenantCard(
                                leadTenant = tenant,
                                onCardClick = { onViewTenantDetails(tenant.id!!) },
                                onEditLeadTenant = { onRegisterTenant(tenant.id) },
                                onDeleteLeadTenant = { viewModel.onDeleteTenant(tenant = tenant) },
                                onNewAssistance = { onRegisterAssistance(tenant.id!!, null) },
                                onEditAssistance = {
                                    onRegisterAssistance(tenant.id!!, unfinishedAssistance?.id!!)
                                },
                                onDeleteAssistance = {
                                    onDeleteAssistance(unfinishedAssistance!!.copy(tenant = tenant))
                                },
                                onViewStay = { onViewStay(unfinishedStay?.id!!) },
                                onNewStay = { onNewStay(tenant.id!!) },
                                hasUnfinishedAssistance = unfinishedAssistance != null,
                                hasUnfinishedStay = unfinishedStay != null,
                                qtGuestsLabelText = when {
                                    unfinishedStay?.qtGuests != null && unfinishedStay.qtGuests > 0 -> {
                                        pluralStringResource(
                                            id = R.plurals.tenant_stay_qt_guests_label,
                                            count = unfinishedStay.qtGuests,
                                            unfinishedStay.qtGuests,
                                        )
                                    }

                                    else -> null
                                },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }
    if (tenantToDelete != null) {
        ConfirmDeleteTenantDialog(
            onConfirmDelete = viewModel::deleteTenant,
            onCancel = { viewModel.onDeleteTenant(tenant = null) },
        )
    }
}
