package com.fstengineering.leasilycrm.propertyDashboard.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel.UiEvent.NavigateUp
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel.UiEvent.PropertyDeleteSuccess
import com.fstengineering.leasilycrm.propertyDashboard.presentation.composables.PropertyDashboardSection
import com.fstengineering.leasilycrm.propertyDashboard.presentation.composables.PropertyDashboardTopAppBar
import com.fstengineering.leasilycrm.propertyDashboard.presentation.models.PropertyUiState

@Composable
fun PropertyDashboardScreen(
    propertyId: Long,
    viewModel: PropertyDashboardViewModel,
    onBack: () -> Unit,
    onEditProperty: () -> Unit,
    onViewPropertyDetails: () -> Unit,
    onViewPropertyOwnerDetails: (Long) -> Unit,
    onViewAllPropertyStays: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onViewStay: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val property by viewModel.property.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyId = propertyId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is NavigateUp -> {
                    onBack()
                }

                is PropertyDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_success_message))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            Column {
                PropertyDashboardTopAppBar(
                    onBack = onBack,
                    onEditProperty = onEditProperty,
                    onDeleteProperty = { mustShowConfirmDeleteDialog = true },
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (property) {
            is PropertyUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is PropertyUiState.Loaded -> {
                val loadedProperty = property as PropertyUiState.Loaded

                PropertyDashboardSection(
                    property = loadedProperty,
                    onViewPropertyDetails = onViewPropertyDetails,
                    onViewPropertyOwnerDetails = {
                        onViewPropertyOwnerDetails(loadedProperty.owner.id)
                    },
                    onViewAllPropertyStays = onViewAllPropertyStays,
                    onNewStay = { mustShowStayTypeDialog = true },
                    onViewStay = onViewStay,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }
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

        if (mustShowStayTypeDialog) {
            OccupiedPeriodTypeDialog(
                onTenantStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(false)
                },
                onUnavailablePeriodStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(true)
                },
                onCancel = { mustShowStayTypeDialog = false }
            )
        }
    }
}
