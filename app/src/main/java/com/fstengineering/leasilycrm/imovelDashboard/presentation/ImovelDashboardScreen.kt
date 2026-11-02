package com.fstengineering.leasilycrm.imovelDashboard.presentation

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
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteImovelDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardViewModel.UiEvent.NavigateUp
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardViewModel.UiEvent.ImovelDeleteSuccess
import com.fstengineering.leasilycrm.imovelDashboard.presentation.composables.ImovelDashboardSection
import com.fstengineering.leasilycrm.imovelDashboard.presentation.composables.ImovelDashboardTopAppBar
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelUiState

@Composable
fun ImovelDashboardScreen(
    imovelId: Long,
    viewModel: ImovelDashboardViewModel,
    onBack: () -> Unit,
    onEditImovel: () -> Unit,
    onViewImovelDetails: () -> Unit,
    onViewProprietarioDetails: (Long) -> Unit,
    onViewAllImovelStays: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onViewStay: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val imovel by viewModel.imovel.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(imovelId = imovelId)

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

                is ImovelDeleteSuccess -> {
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
                ImovelDashboardTopAppBar(
                    onBack = onBack,
                    onEditImovel = onEditImovel,
                    onDeleteImovel = { mustShowConfirmDeleteDialog = true },
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (imovel) {
            is ImovelUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ImovelUiState.Loaded -> {
                val loadedImovel = imovel as ImovelUiState.Loaded

                ImovelDashboardSection(
                    imovel = loadedImovel,
                    onViewImovelDetails = onViewImovelDetails,
                    onViewProprietarioDetails = {
                        onViewProprietarioDetails(loadedImovel.proprietario.id)
                    },
                    onViewAllImovelStays = onViewAllImovelStays,
                    onNewStay = { mustShowStayTypeDialog = true },
                    onViewStay = onViewStay,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        if (mustShowConfirmDeleteDialog) {
            ConfirmDeleteImovelDialog(
                onConfirmDelete = {
                    viewModel.deleteImovel()

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
