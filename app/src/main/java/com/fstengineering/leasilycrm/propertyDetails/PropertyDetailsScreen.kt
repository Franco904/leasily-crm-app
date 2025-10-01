package com.fstengineering.leasilycrm.propertyDetails

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.StayTypeDialog
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsViewModel.UiEvents.NavigateUp
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsViewModel.UiEvents.PropertyDeleteSuccess
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyDetails.composables.PropertyDetailsFinancialPage
import com.fstengineering.leasilycrm.propertyDetails.composables.PropertyDetailsIdentificationPage
import com.fstengineering.leasilycrm.propertyDetails.composables.PropertyDetailsTopAppBar
import com.fstengineering.leasilycrm.propertyDetails.models.PropertyDetailsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    propertyId: Long,
    viewModel: PropertyDetailsViewModel,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onEditProperty: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    var selectedTabId by rememberSaveable {
        mutableIntStateOf(PropertyDetailsTab.Identification.id)
    }

    val property by viewModel.property.collectAsStateWithLifecycle()
    val propertyCharacteristics by viewModel.propertyCharacteristics.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

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

                is NavigateUp -> {
                    onBack()
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
            PropertyDetailsTopAppBar(
                onBack = onBack,
                onEditProperty = onEditProperty,
                onDeleteProperty = { mustShowConfirmDeleteDialog = true },
                onNewStay = { mustShowStayTypeDialog = true },
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
                                property = property,
                                propertyCharacteristics = propertyCharacteristics,
                                onViewPropertyPicture = { pictureFile ->
                                    context.openFileWithChooser(
                                        uriString = pictureFile.uriString,
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
                                property = property,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        null -> null
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
            StayTypeDialog(
                onLoggedRealtorStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(false)
                },
                onOtherRealtorStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(true)
                },
                onCancel = { mustShowStayTypeDialog = false }
            )
        }
    }
}
