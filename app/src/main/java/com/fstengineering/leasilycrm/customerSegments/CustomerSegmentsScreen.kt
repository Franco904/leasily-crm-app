package com.fstengineering.leasilycrm.customerSegments

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel.UiEvents.CustomerSegmentCreateSuccess
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel.UiEvents.CustomerSegmentDeleteSuccess
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel.UiEvents.CustomerSegmentEditSuccess
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.customerSegments.composables.ConfirmDeleteCustomerSegmentDialog
import com.fstengineering.leasilycrm.customerSegments.composables.CustomerSegmentCard
import com.fstengineering.leasilycrm.customerSegments.composables.CustomerSegmentRegistrationDialog
import com.fstengineering.leasilycrm.customerSegments.composables.CustomerSegmentsTopAppBar
import com.fstengineering.leasilycrm.customerSegments.composables.PropertyOwnerCustomerSegmentsEmptySection
import com.fstengineering.leasilycrm.customerSegments.composables.TenantCustomerSegmentsEmptySection
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentFormUiState

@Composable
fun CustomerSegmentsScreen(
    viewModel: CustomerSegmentsViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val customerSegmentTypes by viewModel.customerSegmentTypes.collectAsStateWithLifecycle()

    val tenantCustomerSegments by viewModel.tenantCustomerSegments.collectAsStateWithLifecycle()
    val propertyOwnerCustomerSegments by viewModel.propertyOwnerCustomerSegments.collectAsStateWithLifecycle()

    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var isRegisteringNewCustomerSegment by remember { mutableStateOf(false) }
    val customerSegmentForm by viewModel.customerSegmentForm.collectAsStateWithLifecycle()
    val customerSegmentToDelete by viewModel.customerSegmentToDelete.collectAsStateWithLifecycle()

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

                is CustomerSegmentCreateSuccess -> {
                    isRegisteringNewCustomerSegment = false

                    showSnackbar(context.getString(R.string.snackbar_create_customer_segment_succeeded))
                }

                is CustomerSegmentEditSuccess -> {
                    isRegisteringNewCustomerSegment = false

                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is CustomerSegmentDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_customer_segment_succeeded))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            CustomerSegmentsTopAppBar(onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    viewModel.onCustomerSegmentFormChange(form = CustomerSegmentFormUiState())
                    isRegisteringNewCustomerSegment = true
                },
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets(bottom = 0.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (isLoading) {
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
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.customer_segments_tenants_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                if (tenantCustomerSegments.isEmpty()) {
                    item {
                        TenantCustomerSegmentsEmptySection()
                    }
                } else {
                    itemsIndexed(
                        items = tenantCustomerSegments,
                        key = { i, segment -> segment.id ?: i },
                        contentType = { _, entryType -> entryType },
                    ) { _, tenantSegment ->
                        CustomerSegmentCard(
                            segment = tenantSegment,
                            onEditSegment = {
                                viewModel.onCustomerSegmentFormChange(form = tenantSegment)
                            },
                            onDeleteSegment = {
                                viewModel.onDeleteCustomerSegment(customerSegment = tenantSegment)
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                item {
                    Column {
                        Spacer(modifier = Modifier.height(48.dp))
                        Text(
                            text = stringResource(R.string.customer_segments_property_owners_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                if (propertyOwnerCustomerSegments.isEmpty()) {
                    item {
                        PropertyOwnerCustomerSegmentsEmptySection()
                    }
                } else {
                    itemsIndexed(
                        items = propertyOwnerCustomerSegments,
                        key = { i, segment -> segment.id ?: i },
                        contentType = { _, entryType -> entryType },
                    ) { i, propertyOwnerSegment ->
                        CustomerSegmentCard(
                            segment = propertyOwnerSegment,
                            onEditSegment = {
                                viewModel.onCustomerSegmentFormChange(form = propertyOwnerSegment)
                            },
                            onDeleteSegment = {
                                viewModel.onDeleteCustomerSegment(customerSegment = propertyOwnerSegment)
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        if (customerSegmentForm != null) {
            CustomerSegmentRegistrationDialog(
                customerSegmentForm = customerSegmentForm ?: CustomerSegmentFormUiState(),
                onCustomerSegmentFormChange = viewModel::onCustomerSegmentFormChange,
                customerSegmentTypes = customerSegmentTypes,
                formErrors = formErrors.errors,
                isEditing = !isRegisteringNewCustomerSegment,
                onSave = {
                    viewModel.saveCustomerSegment(isEditing = !isRegisteringNewCustomerSegment)
                },
                onCancel = {
                    viewModel.clearErrors()

                    isRegisteringNewCustomerSegment = false
                    viewModel.onCustomerSegmentFormChange(form = null)
                },
            )
        }
        if (customerSegmentToDelete != null) {
            ConfirmDeleteCustomerSegmentDialog(
                onDelete = viewModel::deleteCustomerSegment,
                onCancel = {
                    viewModel.onDeleteCustomerSegment(customerSegment = null)
                },
            )
        }
    }
}
