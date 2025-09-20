package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel.UiEvents.ValidateSuccess
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationStep1Screen(
    assistanceId: Long?,
    leadTenantId: Long,
    parentViewModel: AssistanceRegistrationViewModel,
    stepViewModel: AssistanceRegistrationStep1ViewModel,
    onLeaveRegistration: () -> Unit,
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
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val leadTenantName by parentViewModel.leadTenantName.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()
    val seaProximityTypes by parentViewModel.seaProximityTypes.collectAsStateWithLifecycle()
    val assistancePropertyCharacteristics by parentViewModel.assistancePropertyCharacteristics
        .collectAsStateWithLifecycle()

    val scrollToFieldFlow = Channel<AssistanceStep1Field>()

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        parentViewModel.init(
            assistanceId = assistanceId,
            leadTenantId = leadTenantId,
        )

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
                    is AssistanceRegistrationStep1ViewModel.UiEvents.DataSourceError -> {
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
            AssistanceRegistrationTopAppBar(
                leadTenantName = leadTenantName,
                isEditing = parentViewModel.assistanceId != null,
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
            AssistanceRegistrationFormSectionStep1(
                assistanceForm = assistanceForm,
                onAssistanceFormChange = { newAssistance ->
                    parentViewModel.onAssistanceFormChange(newAssistance)
                    stepViewModel.onAssistanceFormChange(newAssistance, assistanceForm)
                },
                pastNeighborhoods = pastNeighborhoods,
                seaProximityTypes = seaProximityTypes,
                assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                formErrors = formErrors.errors,
                scrollToFieldFlow = scrollToFieldFlow.receiveAsFlow(),
                onNext = {
                    focusManager.clearFocus()

                    stepViewModel.validateAssistanceStep1(
                        assistanceForm = assistanceForm,
                    )
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
}
