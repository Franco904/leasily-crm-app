package com.fstengineering.leasilycrm.propertyRegistration.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.entities.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyPriceValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.propertyRegistration.models.PropertyPriceErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyPriceField
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep3ViewModel(
    private val propertyPriceValidator: PropertyPriceValidator,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _priceFormErrors = MutableStateFlow(PropertyPriceErrorUiState())
    val priceFormErrors = _priceFormErrors.asStateFlow()

    private val _propertyPriceForm = MutableStateFlow<PropertyPrice?>(null)
    val propertyPriceForm = _propertyPriceForm.asStateFlow()

    private var propertyPriceToEdit: PropertyPrice? = null

    private val _propertyPriceToDelete = MutableStateFlow<PropertyPrice?>(null)
    val propertyPriceToDelete = _propertyPriceToDelete.asStateFlow()

    fun onPropertyPriceFormChange(
        form: PropertyPrice?,
        isStartingEdition: Boolean = false,
    ) {
        if (isStartingEdition) propertyPriceToEdit = form

        _priceFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                val hasDateRangeChanged = form?.startDate != propertyPriceForm.value?.startDate
                        || form?.endDate != propertyPriceForm.value?.endDate
                if (hasDateRangeChanged) {
                    map[PropertyPriceField.DateRange] = null
                }

                if (form?.amount != propertyPriceForm.value?.amount) {
                    map[PropertyPriceField.Amount] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _propertyPriceForm.update { form }
    }

    fun validatePropertyPrice(
        otherPropertyPrices: List<PropertyPrice>,
    ) {
        val newPropertyPrice = propertyPriceForm.value ?: return

        viewModelScope.launch {
            propertyPriceValidator.validate(
                propertyPrice = newPropertyPrice,
                propertyPriceBeforeEdit = propertyPriceToEdit,
                otherPropertyPrices = otherPropertyPrices,
            )
                .onErrors { errors -> setFieldErrors(errors) }
                .onSuccess { _uiEvents.send(UiEvents.PropertyPriceValidateSuccess) }
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _priceFormErrors.update { PropertyPriceErrorUiState() }
        delay(150.milliseconds)

        val priceDateRangeError =
            errors.firstOrNull { it is DomainError.PropertyPricePeriod }
        val priceAmountError =
            errors.firstOrNull { it is DomainError.PropertyPriceAmount }

        _priceFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyPriceField.DateRange] = priceDateRangeError?.toInlineMessage()
                map[PropertyPriceField.Amount] = priceAmountError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }
    }

    fun clearErrors() {
        _priceFormErrors.update { PropertyPriceErrorUiState() }
    }

    fun onDeletePropertyPrice(
        price: PropertyPrice?,
    ) {
        _propertyPriceToDelete.update { price }
    }

    sealed interface UiEvents {
        data object PropertyPriceValidateSuccess : UiEvents
    }
}
