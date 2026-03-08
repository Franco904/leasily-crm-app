package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel.UiEvent.Exit
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel.UiEvent.PropertyOwnerCreateSuccess
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel.UiEvent.PropertyOwnerEditSuccess
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel.UiEvent.ScrollToField
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.composables.PropertyOwnerRegistrationFormSection
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.composables.PropertyOwnerRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyOwnerRegistrationScreen(
    propertyOwnerId: Long?,
    viewModel: PropertyOwnerRegistrationViewModel,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by viewModel.draftFormUiState.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<PropertyOwnerField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val openDocumentFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { contentUri ->
            if (contentUri == null) return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            viewModel.onPropertyOwnerFilePick(
                contentUriString = contentUri.toString(),
                defaultFileName = context.getString(R.string.unknown_text),
            )
        }
    )

    LaunchedEffect(Unit) {
        viewModel.init(propertyOwnerId = propertyOwnerId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ScrollToField -> {
                    scrollToFieldChannel.trySend(uiEvent.field)
                }

                is PropertyOwnerCreateSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(R.string.create_property_owner_success_message))
                }

                is PropertyOwnerEditSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is Exit -> {
                    if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                    else {
                        mustShowConfirmLeaveRegistrationDialog = false
                        onBack()
                    }
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
        viewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                PropertyOwnerRegistrationTopAppBar(
                    isEditing = propertyOwnerId != null,
                    onBack = viewModel::onExit,
                    onSavePropertyOwner = viewModel::savePropertyOwner,
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        when (draftForm) {
            is PropertyOwnerDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is PropertyOwnerDraftFormUiState.Loaded -> {
                PropertyOwnerRegistrationFormSection(
                    propertyOwnerForm = draftForm as PropertyOwnerDraftFormUiState.Loaded,
                    onPropertyOwnerFormChange = viewModel::onPropertyOwnerFormChange,
                    formErrors = formErrors.errors,
                    onLastTextFieldDone = { focusManager.clearFocus() },
                    onAddPropertyOwnerFile = {
                        val acceptedMimeTypes = arrayOf("application/*", "text/*")
                        openDocumentFile.launch(acceptedMimeTypes)
                    },
                    scrollToFieldFlow = scrollToFieldFlow,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = propertyOwnerId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onBack()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
