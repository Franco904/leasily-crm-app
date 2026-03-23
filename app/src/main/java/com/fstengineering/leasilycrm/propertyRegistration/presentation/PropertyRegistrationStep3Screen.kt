package com.fstengineering.leasilycrm.propertyRegistration.presentation

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.ConfirmDeletePropertyPriceDialog
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyPriceRegistrationDialog
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyPriceDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep3ViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationStep3Screen(
    parentViewModel: PropertyRegistrationViewModel,
    stepViewModel: PropertyRegistrationStep3ViewModel,
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

    var isRegisteringNewPropertyPrice by remember { mutableStateOf(false) }

    val propertyPriceForm by stepViewModel.propertyPriceForm.collectAsStateWithLifecycle()
    var propertyPriceFormStartDate: AppDate? by rememberSaveable { mutableStateOf(null) }

    val propertyPriceToDelete by stepViewModel.propertyPriceToDelete.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is PropertyRegistrationViewModel.UiEvent.BottomSheetError -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is PropertyRegistrationViewModel.UiEvent.Exit -> {
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
                    is PropertyRegistrationStep3ViewModel.UiEvent.PropertyPriceValidateSuccess -> {
                        if (propertyPriceForm == null) return@collect
                        val loadedForm =
                            draftForm as? PropertyDraftFormUiState.Loaded ?: return@collect

                        val newPrice = propertyPriceForm!!.toSavedUiState()
                        val allPrices =
                            loadedForm.pricesByMonthYear.values.flatten().toMutableList()

                        val i =
                            allPrices.indexOfFirst { it.startDate == propertyPriceFormStartDate }

                        if (i != -1) allPrices[i] = newPrice
                        else allPrices.add(newPrice)

                        val newPrices =
                            loadedForm.prices.updatePrices(allPrices.toPersistentList())
                        val newForm = loadedForm.copy(prices = newPrices)

                        parentViewModel.onPropertyFormChange(newForm)
                        stepViewModel.onPropertyPriceFormChange(form = null)

                        isRegisteringNewPropertyPrice = false
                        propertyPriceFormStartDate = null
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
                PropertyRegistrationTopAppBar(
                    isEditing = parentViewModel.propertyId != null,
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
            is PropertyDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is PropertyDraftFormUiState.Loaded -> {
                val currentForm = draftForm as PropertyDraftFormUiState.Loaded

                PropertyRegistrationFormSectionStep3(
                    form = currentForm,
                    onFormChange = parentViewModel::onPropertyFormChange,
                    onAddPropertyPrice = {
                        stepViewModel.onPropertyPriceFormChange(form = PropertyPriceDraftFormUiState())
                        isRegisteringNewPropertyPrice = true
                    },
                    onEditPropertyPrice = {
                        propertyPriceFormStartDate = it.startDate
                        stepViewModel.onPropertyPriceFormChange(
                            form = PropertyPriceDraftFormUiState.fromSavedUiState(it),
                            isStartingEdition = true,
                        )
                    },
                    onDeletePropertyPrice = stepViewModel::onDeletePropertyPrice,
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

        if (propertyPriceForm != null && draftForm is PropertyDraftFormUiState.Loaded) {
            val form = draftForm as PropertyDraftFormUiState.Loaded
            val allPrices = form.pricesByMonthYear.values.flatten().toPersistentList()

            PropertyPriceRegistrationDialog(
                priceForm = propertyPriceForm!!,
                onPriceFormChange = stepViewModel::onPropertyPriceFormChange,
                currentPrices = allPrices,
                formErrors = priceFormErrors.errors,
                isEditing = !isRegisteringNewPropertyPrice,
                onSave = { stepViewModel.validatePropertyPrice(allPrices = allPrices) },
                onCancel = {
                    isRegisteringNewPropertyPrice = false
                    propertyPriceFormStartDate = null

                    stepViewModel.onPropertyPriceFormChange(form = null)
                    stepViewModel.clearErrors()
                },
            )
        }

        if (propertyPriceToDelete != null && draftForm is PropertyDraftFormUiState.Loaded) {
            val form = draftForm as PropertyDraftFormUiState.Loaded
            val allPrices = form.pricesByMonthYear.values.flatten().toMutableList()

            ConfirmDeletePropertyPriceDialog(
                onConfirmDelete = {
                    val priceToDelete = allPrices
                        .find { it.startDate == propertyPriceToDelete?.startDate }
                        ?: return@ConfirmDeletePropertyPriceDialog

                    allPrices.remove(priceToDelete)

                    val newPrices =
                        form.prices.updatePrices(allPrices.toPersistentList())
                    val newForm = form.copy(prices = newPrices)

                    parentViewModel.onPropertyFormChange(newForm)
                    stepViewModel.onDeletePropertyPrice(price = null)
                },
                onCancel = {
                    stepViewModel.onDeletePropertyPrice(price = null)
                },
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
