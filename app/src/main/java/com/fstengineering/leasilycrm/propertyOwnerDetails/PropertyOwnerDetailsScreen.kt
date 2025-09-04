package com.fstengineering.leasilycrm.propertyOwnerDetails

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
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyOwnerDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openToPlayStoreApp
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel.UiEvents.PropertyOwnerDeleteSuccess
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyOwnerDetails.composables.PropertyOwnerDetailsSection
import com.fstengineering.leasilycrm.propertyOwnerDetails.composables.PropertyOwnerDetailsTopAppBar

@Composable
fun PropertyOwnerDetailsScreen(
    propertyOwnerId: Int,
    viewModel: PropertyOwnerDetailsViewModel,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onEditPropertyOwner: (Int) -> Unit,
    showSnackbar: (String) -> Unit,
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
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is PropertyOwnerDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_property_owner_succeeded))
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
            PropertyOwnerDetailsTopAppBar(
                onBack = onBack,
                onEditPropertyOwner = { onEditPropertyOwner(propertyOwnerId) },
                onDeletePropertyOwner = { mustShowConfirmDeleteDialog = true },
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
                onOpenPlayStore = { context.openToPlayStoreApp() },
                onClose = { mustShowNoAppToShowFileContentDialog = false }
            )
        }
    }
}
