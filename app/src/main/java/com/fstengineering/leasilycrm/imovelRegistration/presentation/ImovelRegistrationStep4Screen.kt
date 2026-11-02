package com.fstengineering.leasilycrm.imovelRegistration.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationFormSectionStep4
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationTopAppBar
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelRegistrationStep4Screen(
    parentViewModel: ImovelRegistrationViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val pickMultipleMedia = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(),
        onResult = { contentUris ->
            contentUris.forEach { contentUri ->
                context.contentResolver.takePersistableUriPermission(
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )

                parentViewModel.onImovelFilePick(contentUriString = contentUri.toString())
            }
        }
    )

    LaunchedEffect(Unit) {
        parentViewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is ImovelRegistrationViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ImovelRegistrationViewModel.UiEvent.ImovelCreateSuccess -> {
                    onLeaveRegistration()
                    onUiEvent(UiEvent.ShowSnackBar(R.string.create_property_success_message))
                }

                is ImovelRegistrationViewModel.UiEvent.ImovelEditSuccess -> {
                    onLeaveRegistration()
                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is ImovelRegistrationViewModel.UiEvent.Exit -> {
                    if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                    else {
                        mustShowConfirmLeaveRegistrationDialog = false
                        onLeaveRegistration()
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
        parentViewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                ImovelRegistrationTopAppBar(
                    isEditing = parentViewModel.imovelId != null,
                    onBack = parentViewModel::onExit,
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
            is ImovelDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ImovelDraftFormUiState.Loaded -> {
                val currentForm = draftForm as ImovelDraftFormUiState.Loaded

                ImovelRegistrationFormSectionStep4(
                    form = currentForm,
                    onFormChange = parentViewModel::onImovelFormChange,
                    onAddImovelPicture = {
                        pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    onBack = onBack,
                    onSave = {
                        focusManager.clearFocus()
                        parentViewModel.saveImovel()
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.imovelId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
