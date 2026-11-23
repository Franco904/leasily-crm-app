package com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import com.fstengineering.leasilycrm.imovelRegistration.domain.valueObjects.ImovelPricePeriod
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelPriceDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelPriceErrorUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelPriceField
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ImovelRegistrationStep3ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _priceFormErrors = MutableStateFlow(ImovelPriceErrorUiState())
    val priceFormErrors = _priceFormErrors.asStateFlow()

    private val _imovelPriceForm = MutableStateFlow<ImovelPriceDraftFormUiState?>(null)
    val imovelPriceForm = _imovelPriceForm.asStateFlow()

    private var imovelPriceToEdit: ImovelPriceDraftFormUiState? = null

    private val _imovelPriceToDelete = MutableStateFlow<ImovelPriceUiState?>(null)
    val imovelPriceToDelete = _imovelPriceToDelete.asStateFlow()

    fun onImovelPriceFormChange(
        form: ImovelPriceDraftFormUiState?,
        isStartingEdition: Boolean = false,
    ) {
        if (isStartingEdition) imovelPriceToEdit = form

        _priceFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                val hasDateRangeChanged = form?.startDate != imovelPriceForm.value?.startDate
                        || form?.endDate != imovelPriceForm.value?.endDate
                if (hasDateRangeChanged) {
                    map[ImovelPriceField.Period] = null
                }

                if (form?.amount != imovelPriceForm.value?.amount) {
                    map[ImovelPriceField.Amount] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _imovelPriceForm.update { form }
    }

    fun validateImovelPrice(allPrices: List<ImovelPriceUiState>) {
        val form = imovelPriceForm.value ?: return

        val otherPeriods = allPrices
            .filter { it.startDate != imovelPriceToEdit?.startDate }
            .mapNotNull {
                val result = ImovelPricePeriod.parse(it.startDate, it.endDate)
                (result as? AppResult.Success)?.data
            }

        viewModelScope.launch {
            ImovelPrice.parse(
                startDate = form.startDate,
                endDate = form.endDate,
                amount = form.amount,
                otherPeriods = otherPeriods,
            )
                .onSuccess { _uiEvents.trySend(UiEvent.ImovelPriceValidateSuccess) }
                .onError { error -> setFieldErrors(error.toDomainErrors()) }
        }
    }

    private fun setFieldErrors(errors: List<DomainError>) {
        _priceFormErrors.update { ImovelPriceErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val priceDateRangeError =
                errors.firstOrNull { it is DomainError.ImovelPricePeriod }
            val priceAmountError =
                errors.firstOrNull { it is DomainError.Amount }

            _priceFormErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[ImovelPriceField.Period] = priceDateRangeError?.toInlineMessage()
                    map[ImovelPriceField.Amount] = priceAmountError?.toInlineMessage()
                }

                it.copy(errors = newErrors)
            }

            if (!_priceFormErrors.value.hasErrors) {
                _uiEvents.trySend(UiEvent.ImovelPriceValidateSuccess)
            }
        }
    }

    fun clearErrors() {
        _priceFormErrors.update { ImovelPriceErrorUiState() }
    }

    fun onDeleteImovelPrice(price: ImovelPriceUiState?) {
        _imovelPriceToDelete.update { price }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data object ImovelPriceValidateSuccess : UiEvent
    }
}
