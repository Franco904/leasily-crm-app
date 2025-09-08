package com.fstengineering.leasilycrm.propertyRegistration

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.PropertyCreateSuccess
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.PropertyEditSuccess
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationFormSectionStep4
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationStep4Screen(
    parentViewModel: PropertyRegistrationViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val propertyForm by parentViewModel.propertyForm.collectAsStateWithLifecycle()

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val pickMultipleMedia = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(),
        onResult = { contentUris ->
            contentUris.forEach { contentUri ->
                context.contentResolver.takePersistableUriPermission(
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )

                parentViewModel.onPropertyFilePick(contentUriString = contentUri.toString())
            }
        }
    )

    LaunchedEffect(Unit) {
        parentViewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is PropertyCreateSuccess -> {
                    onLeaveRegistration()
                    showSnackbar(context.getString(R.string.snackbar_create_property_succeeded))
                }

                is PropertyEditSuccess -> {
                    onLeaveRegistration()
                    showSnackbar(context.getString(uiEvent.messageId))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    BackHandler {
        mustShowConfirmLeaveRegistrationDialog = true
    }

    Scaffold(
        topBar = {
            PropertyRegistrationTopAppBar(
                isEditing = parentViewModel.propertyId != null,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
            )
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
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
            PropertyRegistrationFormSectionStep4(
                propertyForm = propertyForm,
                onPropertyFormChange = parentViewModel::onPropertyFormChange,
                onAddPropertyPicture = {
                    pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                },
                onBack = onBack,
                onSave = {
                    focusManager.clearFocus()
                    parentViewModel.saveProperty()
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            )
        }
        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.propertyId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false

                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
