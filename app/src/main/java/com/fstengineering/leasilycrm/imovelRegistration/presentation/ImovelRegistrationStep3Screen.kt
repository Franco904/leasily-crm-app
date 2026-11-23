package com.fstengineering.leasilycrm.imovelRegistration.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ConfirmDeleteImovelPriceDialog
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelPriceRegistrationDialog
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationTopAppBar
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelPriceDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels.ImovelRegistrationStep3ViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelRegistrationStep3Screen(
    parentViewModel: ImovelRegistrationViewModel,
    stepViewModel: ImovelRegistrationStep3ViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val priceFormErrors by stepViewModel.priceFormErrors.collectAsStateWithLifecycle()

    var isRegisteringNewImovelPrice by remember { mutableStateOf(false) }

    val imovelPriceForm by stepViewModel.imovelPriceForm.collectAsStateWithLifecycle()
    var imovelPriceFormStartDate: AppDate? by rememberSaveable { mutableStateOf(null) }

    val imovelPriceToDelete by stepViewModel.imovelPriceToDelete.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is ImovelRegistrationViewModel.UiEvent.ShowErrorBottomSheet -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is ImovelRegistrationViewModel.UiEvent.Exit -> {
                        if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                        else {
                            mustShowConfirmLeaveRegistrationDialog = false
                            onLeaveRegistration()
                        }
                    }

                    else -> {}
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is ImovelRegistrationStep3ViewModel.UiEvent.FinishImovelPriceValidation -> {
                        if (imovelPriceForm == null) return@collect
                        val loadedForm =
                            draftForm as? ImovelDraftFormUiState.Loaded ?: return@collect

                        val newPrice = imovelPriceForm!!.toSavedUiState()
                        val allPrices =
                            loadedForm.pricesByMonthYear.values.flatten().toMutableList()

                        val i =
                            allPrices.indexOfFirst { it.startDate == imovelPriceFormStartDate }

                        if (i != -1) allPrices[i] = newPrice
                        else allPrices.add(newPrice)

                        val newPrices =
                            loadedForm.prices.updatePrices(allPrices.toPersistentList())
                        val newForm = loadedForm.copy(prices = newPrices)

                        parentViewModel.onImovelFormChange(newForm)
                        stepViewModel.onImovelPriceFormChange(form = null)

                        isRegisteringNewImovelPrice = false
                        imovelPriceFormStartDate = null
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
                    isEditing = parentViewModel.isEditing,
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

                ImovelRegistrationFormSectionStep3(
                    form = currentForm,
                    onFormChange = parentViewModel::onImovelFormChange,
                    onAddImovelPrice = {
                        stepViewModel.onImovelPriceFormChange(form = ImovelPriceDraftFormUiState())
                        isRegisteringNewImovelPrice = true
                    },
                    onEditImovelPrice = {
                        imovelPriceFormStartDate = it.startDate
                        stepViewModel.onImovelPriceFormChange(
                            form = ImovelPriceDraftFormUiState.fromSavedUiState(it),
                            isStartingEdition = true,
                        )
                    },
                    onDeleteImovelPrice = stepViewModel::onDeleteImovelPrice,
                    onBack = onBack,
                    onNext = {
                        focusManager.clearFocus()
                        onNext()
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }

        if (imovelPriceForm != null && draftForm is ImovelDraftFormUiState.Loaded) {
            val form = draftForm as ImovelDraftFormUiState.Loaded
            val allPrices = form.pricesByMonthYear.values.flatten().toPersistentList()

            ImovelPriceRegistrationDialog(
                priceForm = imovelPriceForm!!,
                onPriceFormChange = stepViewModel::onImovelPriceFormChange,
                currentPrices = allPrices,
                formErrors = priceFormErrors.errors,
                isEditing = !isRegisteringNewImovelPrice,
                onSave = { stepViewModel.validateImovelPrice(allPrices = allPrices) },
                onCancel = {
                    isRegisteringNewImovelPrice = false
                    imovelPriceFormStartDate = null

                    stepViewModel.onImovelPriceFormChange(form = null)
                    stepViewModel.clearErrors()
                },
            )
        }

        if (imovelPriceToDelete != null && draftForm is ImovelDraftFormUiState.Loaded) {
            val form = draftForm as ImovelDraftFormUiState.Loaded
            val allPrices = form.pricesByMonthYear.values.flatten().toMutableList()

            ConfirmDeleteImovelPriceDialog(
                onConfirmDelete = {
                    val priceToDelete = allPrices
                        .find { it.startDate == imovelPriceToDelete?.startDate }
                        ?: return@ConfirmDeleteImovelPriceDialog

                    allPrices.remove(priceToDelete)

                    val newPrices =
                        form.prices.updatePrices(allPrices.toPersistentList())
                    val newForm = form.copy(prices = newPrices)

                    parentViewModel.onImovelFormChange(newForm)
                    stepViewModel.onDeleteImovelPrice(price = null)
                },
                onCancel = {
                    stepViewModel.onDeleteImovelPrice(price = null)
                },
            )
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.isEditing,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
