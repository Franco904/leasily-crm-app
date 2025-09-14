package com.fstengineering.leasilycrm.home.leads

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByMenuOption
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByTextButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.LeadTenantCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteLeadDialog
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrdenationType
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel.UiEvents.LeadDeleteSuccess
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.leads.composables.LeadsEmptySection
import kotlinx.collections.immutable.persistentListOf

@Composable
fun LeadsPage(
    viewModel: LeadsViewModel,
    onRegisterLead: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val lazyListState = rememberLazyListState()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val leadToDelete by viewModel.leadToDelete.collectAsStateWithLifecycle()

    val ordenationType by viewModel.ordenationType.collectAsStateWithLifecycle()

    var isSearchingLead by remember { mutableStateOf(false) }
    val searchInput by viewModel.searchInput.collectAsStateWithLifecycle()

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
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is LeadDeleteSuccess -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
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
                                    ordenationType = ordenationType,
                                    orderByMenuOptions = persistentListOf(
                                        OrderByMenuOption(
                                            text = stringResource(R.string.leads_order_by_name_text),
                                            onClick = {
                                                viewModel.onOrdenationTypeChange(
                                                    LeadTenantOrdenationType.Name,
                                                )
                                            },
                                        ),
                                        OrderByMenuOption(
                                            text = stringResource(R.string.leads_order_by_last_update_date_text),
                                            onClick = {
                                                viewModel.onOrdenationTypeChange(
                                                    LeadTenantOrdenationType.LastUpdateDate,
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
                            key = { _, lead -> lead.id!! },
                            contentType = { _, entryType -> entryType },
                        ) { _, lead ->
                            LeadTenantCard(
                                leadTenant = lead,
                                onViewLeadTenantDetails = {},
                                onEditLeadTenant = { onRegisterLead(lead.id) },
                                onDeleteLeadTenant = { viewModel.onDeleteLead(lead = lead) },
                                onNewAssistance = {},
                                onViewAssistance = {},
                                onNewPropertyStay = {},
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
                onCancel = {
                    viewModel.onDeleteLead(lead = null)
                },
            )
        }
    }
}
