package com.fstengineering.leasilycrm.propertyOwnerDetails

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
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
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyOwnerDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel.UiEvents.PropertyOwnerDeleteSuccess
import com.fstengineering.leasilycrm.propertyOwnerDetails.composables.PropertyOwnerDetailsSection
import com.fstengineering.leasilycrm.propertyOwnerDetails.composables.PropertyOwnerDetailsTopAppBar

@Composable
fun PropertyOwnerDetailsScreen(
    propertyOwnerId: Long,
    viewModel: PropertyOwnerDetailsViewModel,
    onBack: () -> Unit,
    onEditPropertyOwner: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val propertyOwner by viewModel.propertyOwner.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyOwnerId = propertyOwnerId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is PropertyOwnerDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_owner_success_message))
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
                PropertyOwnerDetailsTopAppBar(
                    onBack = onBack,
                    onEditPropertyOwner = { onEditPropertyOwner(propertyOwnerId) },
                    onDeletePropertyOwner = { mustShowConfirmDeleteDialog = true },
                )
                HorizontalDivider()
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
            PropertyOwnerDetailsSection(
                propertyOwner = propertyOwner,
                onOpenPropertyOwnerFile = { file ->
                    context.openFileWithChooser(
                        uriString = file.uriString,
                        onActivityNotFound = { mustShowNoAppToShowFileContentDialog = true }
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            )
        }
        if (mustShowConfirmDeleteDialog) {
            ConfirmDeletePropertyOwnerDialog(
                onConfirmDelete = {
                    viewModel.deletePropertyOwner()

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
    }
}
