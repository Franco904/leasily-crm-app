package com.fstengineering.leasilycrm.imovelOcupacao.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.ImovelOcupacaoViewModel.UiEvent.NavigateUp
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.ImovelOcupacaoViewModel.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.composables.ImovelOcupacaoTopAppBar
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.composables.ImovelOccupiedDatesSection
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelPeriodosOcupadosUiState

@Composable
fun ImovelOcupacaoScreen(
    viewModel: ImovelOcupacaoViewModel,
    onBack: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onViewStay: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val occupiedPeriods by viewModel.occupiedPeriods.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
            when (uiEvent) {
                is ShowErrorBottomSheet -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is NavigateUp -> {
                    onBack()
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
                ImovelOcupacaoTopAppBar(onBack = onBack)
                HorizontalDivider()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowStayTypeDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on),
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
        when (occupiedPeriods) {
            is ImovelPeriodosOcupadosUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ImovelPeriodosOcupadosUiState.Loaded -> {
                ImovelOccupiedDatesSection(
                    occupiedPeriods = occupiedPeriods as ImovelPeriodosOcupadosUiState.Loaded,
                    selectedYear = selectedYear,
                    onYearChange = viewModel::onYearChange,
                    onViewPeriod = onViewStay,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }
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
