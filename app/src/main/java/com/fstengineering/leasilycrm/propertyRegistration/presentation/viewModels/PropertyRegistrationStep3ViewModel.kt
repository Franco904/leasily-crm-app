package com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyPrice
import com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects.PropertyPricePeriod
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyPriceDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyPriceErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyPriceField
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep3ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _priceFormErrors = MutableStateFlow(PropertyPriceErrorUiState())
    val priceFormErrors = _priceFormErrors.asStateFlow()

    private val _propertyPriceForm = MutableStateFlow<PropertyPriceDraftFormUiState?>(null)
    val propertyPriceForm = _propertyPriceForm.asStateFlow()

    private var propertyPriceToEdit: PropertyPriceDraftFormUiState? = null

    private val _propertyPriceToDelete = MutableStateFlow<PropertyPriceUiState?>(null)
    val propertyPriceToDelete = _propertyPriceToDelete.asStateFlow()

    fun onPropertyPriceFormChange(
        form: PropertyPriceDraftFormUiState?,
        isStartingEdition: Boolean = false,
    ) {
        if (isStartingEdition) propertyPriceToEdit = form

        _priceFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                val hasDateRangeChanged = form?.startDate != propertyPriceForm.value?.startDate
                        || form?.endDate != propertyPriceForm.value?.endDate
                if (hasDateRangeChanged) {
                    map[PropertyPriceField.Period] = null
                }

                if (form?.amount != propertyPriceForm.value?.amount) {
                    map[PropertyPriceField.Amount] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _propertyPriceForm.update { form }
    }

    fun validatePropertyPrice(allPrices: List<PropertyPriceUiState>) {
        val form = propertyPriceForm.value ?: return

        val otherPeriods = allPrices
            .filter { it.startDate != propertyPriceToEdit?.startDate }
            .mapNotNull {
                val result = PropertyPricePeriod.parse(it.startDate, it.endDate)
                (result as? AppResult.Success)?.data
            }

        viewModelScope.launch {
            PropertyPrice.parse(
                startDate = form.startDate,
                endDate = form.endDate,
                amount = form.amount,
                otherPeriods = otherPeriods,
            )
                .onSuccess { _uiEvents.trySend(UiEvent.PropertyPriceValidateSuccess) }
                .onError { error -> setFieldErrors(error.errors) }
        }
    }

    private fun setFieldErrors(errors: List<DomainError>) {
        _priceFormErrors.update { PropertyPriceErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val priceDateRangeError =
                errors.firstOrNull { it is DomainError.PropertyPricePeriod }
            val priceAmountError =
                errors.firstOrNull { it is DomainError.Amount }

            _priceFormErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[PropertyPriceField.Period] = priceDateRangeError?.toInlineMessage()
                    map[PropertyPriceField.Amount] = priceAmountError?.toInlineMessage()
                }

                it.copy(errors = newErrors)
            }

            if (!_priceFormErrors.value.hasErrors) {
                _uiEvents.trySend(UiEvent.PropertyPriceValidateSuccess)
            }
        }
    }

    fun clearErrors() {
        _priceFormErrors.update { PropertyPriceErrorUiState() }
    }

    fun onDeletePropertyPrice(price: PropertyPriceUiState?) {
        _propertyPriceToDelete.update { price }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data object PropertyPriceValidateSuccess : UiEvent
    }
}
