package com.fstengineering.leasilycrm.propertyRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.entities.PropertyPrice
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.Exit
import com.fstengineering.leasilycrm.propertyRegistration.composables.ConfirmDeletePropertyPriceDialog
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyPriceRegistrationDialog
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep3ViewModel.UiEvents.PropertyPriceValidateSuccess
import kotlinx.collections.immutable.mutate
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

    val propertyForm by parentViewModel.propertyForm.collectAsStateWithLifecycle()
    val priceFormErrors by stepViewModel.priceFormErrors.collectAsStateWithLifecycle()

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var isRegisteringNewPropertyPrice by remember { mutableStateOf(false) }

    val propertyPriceForm by stepViewModel.propertyPriceForm.collectAsStateWithLifecycle()
    var propertyPriceFormStartDate: AppDate? by rememberSaveable { mutableStateOf(null) }

    val propertyPriceToDelete by stepViewModel.propertyPriceToDelete.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is Error -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is Exit -> {
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
                    is PropertyPriceValidateSuccess -> {
                        if (propertyPriceForm == null) return@collect

                        val newPropertyPrices = propertyForm.prices.toPersistentList().mutate {
                            val i = it.indexOfFirst { price ->
                                price.startDate == propertyPriceFormStartDate
                            }

                            if (i != -1) it[i] = propertyPriceForm!!
                            else it.add(propertyPriceForm!!)
                        }

                        parentViewModel.onPropertyFormChange(
                            propertyForm.copy(prices = newPropertyPrices)
                        )

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
            PropertyRegistrationFormSectionStep3(
                propertyForm = propertyForm,
                onPropertyFormChange = parentViewModel::onPropertyFormChange,
                onAddPropertyPrice = {
                    stepViewModel.onPropertyPriceFormChange(form = PropertyPrice())
                    isRegisteringNewPropertyPrice = true
                },
                onEditPropertyPrice = {
                    propertyPriceFormStartDate = it.startDate
                    stepViewModel.onPropertyPriceFormChange(
                        form = it,
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
        if (propertyPriceForm != null) {
            PropertyPriceRegistrationDialog(
                propertyPriceForm = propertyPriceForm!!,
                onPropertyPriceFormChange = stepViewModel::onPropertyPriceFormChange,
                currentPropertyPrices = propertyForm.prices,
                formErrors = priceFormErrors.errors,
                isEditing = !isRegisteringNewPropertyPrice,
                onSave = {
                    stepViewModel.validatePropertyPrice(
                        otherPropertyPrices = propertyForm.prices,
                    )
                },
                onCancel = {
                    isRegisteringNewPropertyPrice = false
                    propertyPriceFormStartDate = null

                    stepViewModel.onPropertyPriceFormChange(form = null)
                    stepViewModel.clearErrors()
                },
            )
        }
        if (propertyPriceToDelete != null) {
            ConfirmDeletePropertyPriceDialog(
                onConfirmDelete = {
                    val priceToDelete = propertyForm.prices
                        .find { it.startDate == propertyPriceToDelete?.startDate }
                        ?: return@ConfirmDeletePropertyPriceDialog

                    val newPropertyForm = propertyForm.copy(
                        prices = if (priceToDelete.id == null) {
                            propertyForm.prices.toPersistentList().remove(priceToDelete)
                        } else {
                            propertyForm.prices.toPersistentList().mutate {
                                val i = propertyForm.prices.indexOf(priceToDelete)
                                it[i] = priceToDelete.copy(isDeleted = true)
                            }
                        },
                    )

                    parentViewModel.onPropertyFormChange(newPropertyForm)
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
