package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.models.AssistanceRegistrationStep2ListState
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationStep2Screen(
    parentViewModel: AssistanceRegistrationViewModel,
    stepViewModel: AssistanceRegistrationStep2ViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSignOut: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val assistanceForm by parentViewModel.assistanceForm.collectAsStateWithLifecycle()
    val leadTenantName by parentViewModel.leadTenantName.collectAsStateWithLifecycle()

    val isLoadingParent by parentViewModel.isLoading.collectAsStateWithLifecycle()
    val isLoadingStep by stepViewModel.isLoading.collectAsStateWithLifecycle()

    val searchInput by stepViewModel.searchInput.collectAsStateWithLifecycle()
    val selectableProperties by stepViewModel.selectableProperties.collectAsStateWithLifecycle()

    var isAddingSentProperty by remember { mutableStateOf(false) }
    var isSearchingPropertyToSend by remember { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val listStatus by remember(
        isLoadingParent,
        isLoadingStep,
        isAddingSentProperty,
        selectableProperties,
    ) {
        val status = when {
            isLoadingParent || isLoadingStep -> AssistanceRegistrationStep2ListState.Loading
            !isAddingSentProperty -> AssistanceRegistrationStep2ListState.ShowingSelectedSentProperties
            selectableProperties.isEmpty() -> AssistanceRegistrationStep2ListState.Empty
            else -> AssistanceRegistrationStep2ListState.AddingSentProperties
        }

        mutableStateOf(status)
    }

    LaunchedEffect(Unit) {
        stepViewModel.init()

        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is DataSourceError -> {
                        showSnackbar(context.getString(uiEvent.messageId))
                    }

                    is RedirectToSignIn -> {
                        onSignOut()
                    }

                    else -> {}
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is AssistanceRegistrationStep2ViewModel.UiEvents.DataSourceError -> {
                        showSnackbar(context.getString(uiEvent.messageId))
                    }

                    is AssistanceRegistrationStep2ViewModel.UiEvents.RedirectToSignIn -> {
                        onSignOut()
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
        when {
            isSearchingPropertyToSend -> isSearchingPropertyToSend = false
            isAddingSentProperty -> isAddingSentProperty = false
            else -> mustShowConfirmLeaveRegistrationDialog = true
        }
    }

    Scaffold(
        topBar = {
            AssistanceRegistrationTopAppBar(
                leadTenantName = leadTenantName,
                isEditing = parentViewModel.assistanceId != null,
                isAddingSentProperty = isAddingSentProperty,
                mustShowSearchPropertyAction = isAddingSentProperty
                        && selectableProperties.isNotEmpty()
                        && !isSearchingPropertyToSend,
                isSearchingPropertyToSend = isSearchingPropertyToSend,
                onSearchPropertyToSend = { isSearchingPropertyToSend = true },
                searchInput = searchInput,
                onSearchInputChange = stepViewModel::onSearchInputChange,
                onBack = {
                    when {
                        isSearchingPropertyToSend -> {
                            isSearchingPropertyToSend = false
                            stepViewModel.onSearchInputChange(searchInput = null)
                        }

                        isAddingSentProperty -> isAddingSentProperty = false
                        else -> mustShowConfirmLeaveRegistrationDialog = true
                    }
                },
            )
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        AssistanceRegistrationFormSectionStep2(
            listStatus = listStatus,
            assistanceForm = assistanceForm,
            selectableProperties = selectableProperties,
            onAddSentProperty = { isAddingSentProperty = true },
            isSearchingPropertyToSend = isSearchingPropertyToSend,
            onBack = onBack,
            onCancelPropertySelection = { isAddingSentProperty = false },
            onNext = {
                focusManager.clearFocus()
                onNext()
            },
            onSelectProperties = { selectedSentProperties ->
                parentViewModel.onAssistanceFormChange(
                    assistanceForm.copy(sentProperties = selectedSentProperties)
                )

                isAddingSentProperty = false
            },
            modifier = Modifier
                .padding(innerPadding)
        )
    }
    if (mustShowConfirmLeaveRegistrationDialog) {
        ConfirmLeaveRegistrationDialog(
            isEditing = parentViewModel.assistanceId != null,
            onConfirmLeave = {
                mustShowConfirmLeaveRegistrationDialog = false

                onLeaveRegistration()
            },
            onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
        )
    }
}
