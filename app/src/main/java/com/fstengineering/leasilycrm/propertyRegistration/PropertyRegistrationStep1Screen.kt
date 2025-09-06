package com.fstengineering.leasilycrm.propertyRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomsheet
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep1ViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep1ViewModel.UiEvents.ValidateSuccess
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationStep1Screen(
    propertyId: Int?,
    parentViewModel: PropertyRegistrationViewModel,
    stepViewModel: PropertyRegistrationStep1ViewModel,
    onLeaveRegistration: () -> Unit,
    onNext: () -> Unit,
    onSignOut: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val propertyForm by parentViewModel.propertyForm.collectAsStateWithLifecycle()
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val propertyOwners by parentViewModel.propertyOwners.collectAsStateWithLifecycle()
    val propertyTypes by parentViewModel.propertyTypes.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()

    val scrollToFieldFlow = Channel<PropertyStep1Field>()

    val isLoadingParent by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }
    var mustShowPropertyOwnersComboBottomsheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        parentViewModel.init(propertyId = propertyId)

        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is DataSourceError -> {
                        showSnackbar(context.getString(uiEvent.messageId))
                    }

                    is RedirectToSignIn -> {
                        onSignOut()
                    }
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is PropertyRegistrationStep1ViewModel.UiEvents.DataSourceError -> {
                        showSnackbar(context.getString(uiEvent.messageId))
                    }

                    is ScrollToField -> {
                        scrollToFieldFlow.send(uiEvent.field)
                    }

                    is ValidateSuccess -> {
                        onNext()
                    }
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
                isEditing = propertyId != null,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
            )
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        if (isLoadingParent) {
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
            PropertyRegistrationFormSectionStep1(
                propertyForm = propertyForm,
                onPropertyFormChange = { newProperty ->
                    parentViewModel.onPropertyFormChange(newProperty)
                    stepViewModel.onPropertyFormChange(newProperty, propertyForm)
                },
                propertyTypes = propertyTypes,
                pastNeighborhoods = pastNeighborhoods,
                formErrors = formErrors.errors,
                onSelectPropertyOwner = { mustShowPropertyOwnersComboBottomsheet = true },
                onLastTextFieldDone = { focusManager.clearFocus() },
                scrollToFieldFlow = scrollToFieldFlow.receiveAsFlow(),
                onNext = {
                    focusManager.clearFocus()

                    stepViewModel.validatePropertyStep1(
                        propertyForm = propertyForm,
                    )
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            )
        }
        if (mustShowPropertyOwnersComboBottomsheet) {
            AppComboBottomsheet(
                sheetState = sheetState,
                options = propertyOwners.map { it.name ?: "" }.toPersistentList(),
                onOptionSelect = { propertyOwnerName ->
                    val selectedPropertyOwner =
                        propertyOwners.find { it.name == propertyOwnerName }

                    val newProperty = propertyForm.copy(owner = selectedPropertyOwner)
                    parentViewModel.onPropertyFormChange(form = newProperty)
                    stepViewModel.onPropertyFormChange(newProperty, propertyForm)

                    focusManager.clearFocus()
                },
                onDismissRequest = {
                    mustShowPropertyOwnersComboBottomsheet = false
                    focusManager.clearFocus()
                },
                emptyOptionsIcon = Icons.Outlined.Person,
                emptyOptionsText = stringResource(R.string.property_registration_property_owner_empty_text),
            )
        }
        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = propertyId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false

                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
