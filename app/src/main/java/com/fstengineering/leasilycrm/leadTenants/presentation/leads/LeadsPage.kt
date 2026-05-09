package com.fstengineering.leasilycrm.leadTenants.presentation.leads

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByMenuOption
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByTextButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.LeadTenantCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteLeadDialog
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.LeadsViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.LeadsViewModel.UiEvent.LeadDeleteSuccess
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.composables.LeadsEmptySection
import kotlinx.collections.immutable.persistentListOf

@Composable
fun LeadsPage(
    viewModel: LeadsViewModel,
    isSearchingLead: Boolean,
    searchInput: String?,
    onRegisterLead: (Long?) -> Unit,
    onViewLeadDetails: (Long) -> Unit,
    onNewAssistance: (Long) -> Unit,
    onNewStay: (Long) -> Unit,
    onViewStay: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val lazyListState = rememberLazyListState()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val leadToDelete by viewModel.leadToDelete.collectAsStateWithLifecycle()

    val orderType by viewModel.orderType.collectAsStateWithLifecycle()

    val listStatus by remember(isLoading, leads) {
        val status = when {
            isLoading -> ListState.Loading
            leads.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }

        mutableStateOf(status)
    }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is LeadDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_lead_success_message))
                }
            }
        }
    }

    LaunchedEffect(searchInput) {
        viewModel.onSearchInputChange(searchInput = searchInput)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.onPause()
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isSearchingLead) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { onRegisterLead(null) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        AnimatedContent(
            targetState = listStatus,
        ) { listStatus ->
            when (listStatus) {
                ListState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppCircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }

                ListState.Empty -> {
                    LeadsEmptySection(isSearchingLead = isSearchingLead)
                }

                ListState.Idle -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        if (!isSearchingLead) {
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
                            items = leads,
                            key = { _, item -> item.leadTenant.id },
                            contentType = { _, entryType -> entryType },
                        ) { _, item ->
                            LeadTenantCard(
                                leadTenant = item.leadTenant,
                                onCardClick = { onViewLeadDetails(item.leadTenant.id) },
                                onEditLeadTenant = { onRegisterLead(item.leadTenant.id) },
                                onDeleteLeadTenant = { viewModel.onDeleteLead(item.leadTenant.id) },
                                onNewAssistance = { onNewAssistance(item.leadTenant.id) },
                                onViewStay = { item.currentStayId?.let { id -> onViewStay(id) } },
                                onNewStay = { onNewStay(item.leadTenant.id) },
                                hasMoreOptionsMenu = true,
                                hasAssistanceText = true,
                                hasPrimaryActions = true,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
        if (leadToDelete != null) {
            ConfirmDeleteLeadDialog(
                onConfirmDelete = viewModel::deleteLead,
                onCancel = { viewModel.onDeleteLead(null) },
            )
        }
    }
}
