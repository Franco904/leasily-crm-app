package com.fstengineering.leasilycrm.propertyDashboard

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardViewModel.UiEvents.PropertyDeleteSuccess
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyDashboard.composables.PropertyDashboardSection
import com.fstengineering.leasilycrm.propertyDashboard.composables.PropertyDashboardTopAppBar

@Composable
fun PropertyDashboardScreen(
    propertyId: Long,
    viewModel: PropertyDashboardViewModel,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onEditProperty: (Long) -> Unit,
    onViewPropertyDetails: () -> Unit,
    onViewPropertyOwnerDetails: (Long) -> Unit,
    onSeeNextPropertyStays: () -> Unit,
    onNewPropertyStay: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val property by viewModel.property.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyId = propertyId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is PropertyDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_property_succeeded))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            PropertyDashboardTopAppBar(
                onBack = onBack,
                onEditProperty = { onEditProperty(propertyId) },
                onDeleteProperty = { mustShowConfirmDeleteDialog = true },
            )
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
            PropertyDashboardSection(
                property = property,
                onViewPropertyDetails = onViewPropertyDetails,
                onViewPropertyOwnerDetails = {
                    if (property.owner?.id == null) return@PropertyDashboardSection
                    onViewPropertyOwnerDetails(property.owner?.id!!)
                },
                onSeeNextPropertyStays = onSeeNextPropertyStays,
                onNewPropertyStay = onNewPropertyStay,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            )
        }
        if (mustShowConfirmDeleteDialog) {
            ConfirmDeletePropertyDialog(
                onConfirmDelete = {
                    viewModel.deleteProperty()

                    mustShowConfirmDeleteDialog = false
                    onBack()
                },
                onCancel = { mustShowConfirmDeleteDialog = false },
            )
        }
    }
}
