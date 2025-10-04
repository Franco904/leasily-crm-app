package com.fstengineering.leasilycrm.leadTenantDetails

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteCurrentAssistanceUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeadTenantDetailsViewModel(
    private val deleteCurrentAssistanceUseCase: DeleteCurrentAssistanceUseCase,
    private val tenantRepository: TenantRepository,
    private val assistanceCharacteristicRepository: AssistanceCharacteristicRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leadTenant = MutableStateFlow(Tenant())
    val leadTenant = _leadTenant.asStateFlow()

    private val _assistancePropertyCharacteristics =
        MutableStateFlow<ImmutableList<AssistanceCharacteristic>>(persistentListOf())
    val assistancePropertyCharacteristics = _assistancePropertyCharacteristics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchTenantJob: Job? = null

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchSessionExpiration()
        watchLeadTenant(leadTenantId = propertyId)

        getAssistancePropertyCharacteristics()

        isInitialized = true
    }

    private fun watchSessionExpiration() {
        viewModelScope.launch {
            userRepository.watchSessionExpiration().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { hasSessionExpired ->
                        if (hasSessionExpired) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }
                    }
            }
        }
    }

    private fun watchLeadTenant(leadTenantId: Long) {
        _isLoading.update { true }

        watchTenantJob = viewModelScope.launch {
            tenantRepository.watchById(leadTenantId).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { storedLeadTenant ->
                        if (storedLeadTenant == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            watchTenantJob?.cancel()
                            return@onSuccess
                        }

                        _leadTenant.update { storedLeadTenant }
                    }
            }
        }
    }

    private fun getAssistancePropertyCharacteristics() {
        viewModelScope.launch {
            assistanceCharacteristicRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedCharacteristics ->
                    _assistancePropertyCharacteristics.update { storedCharacteristics }
                }

            _isLoading.update { false }
        }
    }

    fun deleteLeadTenant() {
        val leadTenantId = leadTenant.value.id ?: return

        watchTenantJob?.cancel()

        viewModelScope.launch {
            tenantRepository.delete(id = leadTenantId)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.LeadTenantDeleteSuccess)
        }
    }

    fun deleteCurrentAssistance(currentAssistance: Assistance) {
        viewModelScope.launch {
            deleteCurrentAssistanceUseCase(
                currentAssistance = currentAssistance,
                hasFinishedStays = leadTenant.value.hasFinishedStays(),
            )
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object NavigateUp : UiEvents

        data object LeadTenantDeleteSuccess : UiEvents

        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
