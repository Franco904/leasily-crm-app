package com.fstengineering.leasilycrm.imovelDetails.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteImovelDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.ImovelDetailsFinancialPage
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.ImovelDetailsIdentificationPage
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.ImovelDetailsTopAppBar
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelDetailsTab
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelDetailsScreen(
    viewModel: ImovelDetailsViewModel,
    onBack: () -> Unit,
    onEditImovel: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    var selectedTabId by rememberSaveable {
        mutableIntStateOf(ImovelDetailsTab.Identification.id)
    }

    val imovel by viewModel.imovel.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
            when (uiEvent) {
                is ImovelDetailsViewModel.UiEvent.ShowErrorBottomSheet -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ImovelDetailsViewModel.UiEvent.NavigateUp -> {
                    onBack()
                }

                is ImovelDetailsViewModel.UiEvent.ShowDeleteSuccessSnackbar -> {
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
            ImovelDetailsTopAppBar(
                onBack = onBack,
                onEditImovel = onEditImovel,
                onDeleteImovel = { mustShowConfirmDeleteDialog = true },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowStayTypeDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.new_property_stay_action_button_text),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (imovel) {
            is ImovelUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ImovelUiState.Loaded -> {
                Column {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabId - 1,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        ImovelDetailsTab.entries.forEach { tab ->
                            val isTabSelected = selectedTabId == tab.id

                            Tab(
                                selected = isTabSelected,
                                onClick = { selectedTabId = tab.id },
                                text = {
                                    Text(
                                        text = stringResource(tab.labelId),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = when {
                                            isTabSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            )
                        }
                    }
                    AnimatedContent(
                        targetState = ImovelDetailsTab.fromId(selectedTabId)
                    ) { selectedTab ->
                        when (selectedTab) {
                            ImovelDetailsTab.Identification -> {
                                ImovelDetailsIdentificationPage(
                                    imovel = imovel as ImovelUiState.Loaded,
                                    onViewImovelPicture = { pictureFile ->
                                        context.openFileWithChooser(
                                            uriString = pictureFile.uri.toString(),
                                            onActivityNotFound = {
                                                mustShowNoAppToShowFileContentDialog = true
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                )
                            }

                            ImovelDetailsTab.Financial -> {
                                ImovelDetailsFinancialPage(
                                    imovel = imovel as ImovelUiState.Loaded,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
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

        if (mustShowNoAppToShowFileContentDialog) {
            NoAppToShowFileContentDialog(
                onOpenPlayStore = { context.openPlayStoreApp() },
                onClose = { mustShowNoAppToShowFileContentDialog = false }
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
