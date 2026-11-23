package com.fstengineering.leasilycrm.imoveis.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.ImoveisEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.cards.ImovelCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteImovelDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imoveis.presentation.ImoveisViewModel.UiEvent.ShowDeleteSuccessSnackbar
import com.fstengineering.leasilycrm.imoveis.presentation.ImoveisViewModel.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.imoveis.presentation.composables.ImoveisTopAppBar
import com.fstengineering.leasilycrm.imoveis.presentation.models.ImoveisUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ImoveisScreen(
    viewModel: ImoveisViewModel,
    onBack: () -> Unit,
    onRegisterImovel: (Long?) -> Unit,
    onViewImovelDashboard: (Long) -> Unit,
    onViewImovelDetails: (Long) -> Unit,
    onNewStay: (Long, Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imoveis = (uiState as? ImoveisUiState.Loaded)?.imoveis ?: persistentListOf()
    val imovelToDelete by viewModel.imovelToDelete.collectAsStateWithLifecycle()

    var isSearchingImovel by remember { mutableStateOf(false) }
    val searchInput by viewModel.searchInput.collectAsStateWithLifecycle()

    val listStatus by remember(uiState) {
        val status = when (val state = uiState) {
            ImoveisUiState.Loading -> ListState.Loading
            is ImoveisUiState.Loaded if state.imoveis.isEmpty() -> ListState.Empty
            is ImoveisUiState.Loaded -> ListState.Idle
        }

        mutableStateOf(status)
    }

    var imovelIdToRegisterOccupiedPeriod by remember { mutableStateOf<Long?>(null) }

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

                is ShowDeleteSuccessSnackbar -> {
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

    BackHandler {
        if (isSearchingImovel) isSearchingImovel = false
        else onBack()
    }

    Scaffold(
        topBar = {
            Column {
                ImoveisTopAppBar(
                    onBack = {
                        if (isSearchingImovel) {
                            isSearchingImovel = false
                            viewModel.onSearchInputChange(searchInput = null)
                        } else {
                            onBack()
                        }
                    },
                    mustShowSearchImovelAction = imoveis.isNotEmpty() && !isSearchingImovel,
                    isSearchingImovel = isSearchingImovel,
                    onSearchImovel = { isSearchingImovel = true },
                    searchInput = searchInput,
                    onSearchInputChange = viewModel::onSearchInputChange,
                )
                HorizontalDivider()
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                !isSearchingImovel,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { onRegisterImovel(null) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
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
                    FullScreenLoadingIndicator(innerPadding)
                }

                ListState.Empty -> {
                    ImoveisEmptySection(isSearchingImovel = isSearchingImovel)
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
                        item {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.properties_number_of_properties,
                                    imoveis.size,
                                    imoveis.size,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        itemsIndexed(
                            items = imoveis,
                            key = { _, imovel -> imovel.id },
                            contentType = { _, entryType -> entryType },
                        ) { _, imovel ->
                            ImovelCard(
                                imovel = imovel,
                                onCardClick = { onViewImovelDashboard(imovel.id) },
                                onViewImovelDetails = { onViewImovelDetails(imovel.id) },
                                onEditImovel = { onRegisterImovel(imovel.id) },
                                onDeleteImovel = { viewModel.onDeleteImovel(imovel = imovel) },
                                onNewOccupiedPeriod = {
                                    imovelIdToRegisterOccupiedPeriod = imovel.id
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }

        if (imovelToDelete != null) {
            ConfirmDeleteImovelDialog(
                onConfirmDelete = viewModel::deleteImovel,
                onCancel = {
                    viewModel.onDeleteImovel(imovel = null)
                },
            )
        }

        if (imovelIdToRegisterOccupiedPeriod != null) {
            OccupiedPeriodTypeDialog(
                onTenantStayType = {
                    onNewStay(imovelIdToRegisterOccupiedPeriod!!, false)
                    imovelIdToRegisterOccupiedPeriod = null
                },
                onUnavailablePeriodStayType = {
                    onNewStay(imovelIdToRegisterOccupiedPeriod!!, true)
                    imovelIdToRegisterOccupiedPeriod = null
                },
                onCancel = { imovelIdToRegisterOccupiedPeriod = null }
            )
        }
    }
}
