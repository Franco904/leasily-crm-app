package com.fstengineering.leasilycrm.propertyOwnerRegistration

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationViewModel.UiEvents.PropertyOwnerCreateSuccess
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationViewModel.UiEvents.PropertyOwnerEditSuccess
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.propertyOwnerRegistration.composables.PropertyOwnerRegistrationFormSection
import com.fstengineering.leasilycrm.propertyOwnerRegistration.composables.PropertyOwnerRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerField
import kotlinx.collections.immutable.toPersistentList
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

    val sheetState = rememberModalBottomSheetState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val propertyOwnerForm by viewModel.propertyOwnerForm.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val propertyOwnerSegments by viewModel.propertyOwnerSegments.collectAsStateWithLifecycle()

    val scrollToFieldFlow = Channel<PropertyOwnerField>()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }
    var mustShowCustomerSegmentComboBottomsheet by remember { mutableStateOf(false) }

    val openDocumentFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { contentUri ->
            if (contentUri == null) return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            viewModel.onPropertyOwnerFilePick(contentUriString = contentUri.toString())
        }
    )

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

                is ScrollToField -> {
                    scrollToFieldFlow.trySend(uiEvent.field)
                }

                is PropertyOwnerCreateSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(R.string.snackbar_create_property_owner_succeeded))
                }

                is PropertyOwnerEditSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
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
        mustShowConfirmLeaveRegistrationDialog = true
    }

    Scaffold(
        topBar = {
            PropertyOwnerRegistrationTopAppBar(
                isEditing = propertyOwnerId != null,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
                onSavePropertyOwner = viewModel::savePropertyOwner,
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
            PropertyOwnerRegistrationFormSection(
                propertyOwnerForm = propertyOwnerForm,
                onPropertyOwnerFormChange = viewModel::onPropertyOwnerFormChange,
                formErrors = formErrors.errors,
                onCustomerSegmentFieldTap = { mustShowCustomerSegmentComboBottomsheet = true },
                onLastTextFieldDone = { focusManager.clearFocus() },
                onAddPropertyOwnerFile = {
                    val acceptedMimeTypes = arrayOf("application/*", "text/*")
                    openDocumentFile.launch(acceptedMimeTypes)
                },
                scrollToFieldFlow = scrollToFieldFlow.receiveAsFlow(),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            )
        }
        if (mustShowCustomerSegmentComboBottomsheet) {
            AppComboBottomSheet(
                sheetState = sheetState,
                options = propertyOwnerSegments.map { it.name ?: "" }.toPersistentList(),
                onOptionSelect = { customerSegmentName ->
                    val selectedCustomerSegment =
                        propertyOwnerSegments.find { it.name == customerSegmentName }

                    viewModel.onPropertyOwnerFormChange(
                        form = propertyOwnerForm.copy(customerSegment = selectedCustomerSegment)
                    )

                    focusManager.clearFocus()
                },
                onDismissRequest = {
                    mustShowCustomerSegmentComboBottomsheet = false
                    focusManager.clearFocus()
                },
                emptyOptionsIcon = Icons.Default.BookmarkBorder,
                emptyOptionsText = stringResource(R.string.property_owner_registration_customer_segment_empty_text),
            )
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
