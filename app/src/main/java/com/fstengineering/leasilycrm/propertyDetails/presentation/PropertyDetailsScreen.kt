package com.fstengineering.leasilycrm.propertyDetails.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.PropertyDetailsFinancialPage
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.PropertyDetailsIdentificationPage
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.PropertyDetailsTopAppBar
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyDetailsTab
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    propertyId: Long,
    viewModel: PropertyDetailsViewModel,
    onBack: () -> Unit,
    onEditProperty: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    var selectedTabId by rememberSaveable {
        mutableIntStateOf(PropertyDetailsTab.Identification.id)
    }

    val property by viewModel.property.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyId = propertyId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is PropertyDetailsViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is PropertyDetailsViewModel.UiEvent.NavigateUp -> {
                    onBack()
                }

                is PropertyDetailsViewModel.UiEvent.PropertyDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_success_message))
                }
            }
        }
    }

    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.surface,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    )

    Scaffold(
        topBar = {
            PropertyDetailsTopAppBar(
                onBack = onBack,
                onEditProperty = onEditProperty,
                onDeleteProperty = { mustShowConfirmDeleteDialog = true },
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
        when (property) {
            is PropertyUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is PropertyUiState.Loaded -> {
                Column {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabId - 1,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        PropertyDetailsTab.entries.forEach { tab ->
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
                        targetState = PropertyDetailsTab.fromId(selectedTabId)
                    ) { selectedTab ->
                        when (selectedTab) {
                            PropertyDetailsTab.Identification -> {
                                PropertyDetailsIdentificationPage(
                                    property = property as PropertyUiState.Loaded,
                                    onViewPropertyPicture = { pictureFile ->
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

                            PropertyDetailsTab.Financial -> {
                                PropertyDetailsFinancialPage(
                                    property = property as PropertyUiState.Loaded,
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
            ConfirmDeletePropertyDialog(
                onConfirmDelete = {
                    viewModel.deleteProperty()

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
