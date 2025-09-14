package com.fstengineering.leasilycrm.home.leads

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrdenationType
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeadsViewModel(
    private val tenantRepository: TenantRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leads = MutableStateFlow<ImmutableList<Tenant>>(persistentListOf())
    val leads = _leads.asStateFlow()

    private val _leadToDelete = MutableStateFlow<Tenant?>(null)
    val leadToDelete = _leadToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    private val _ordenationType = MutableStateFlow(LeadTenantOrdenationType.Name)
    val ordenationType = _ordenationType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchLeads()
//        watchSearchInput()

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

    private fun watchLeads() {
        _isLoading.update { true }

        viewModelScope.launch {
            val ordenationTypeId = tenantRepository.getPreferredLeadsOrdenationTypeId()
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { ordenationTypeId -> ordenationTypeId },
                )

            _ordenationType.update { LeadTenantOrdenationType.fromId(ordenationTypeId) }

            tenantRepository.watchAllLeads(
                ordenationTypeId = ordenationTypeId,
            ).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { leads ->
                        _leads.update { leads }
                    }

                _isLoading.update { false }
            }
        }
    }

    // TODO: Impl. lead search
//    @OptIn(FlowPreview::class)
//    fun watchSearchInput() {
//        viewModelScope.launch {
//            searchInput
//                .debounce(500.milliseconds)
//                .collect { searchInput ->
//                    _isLoading.update { true }
//
//                    propertyRepository.getAllMatching(matching = searchInput)
//                        .onError { error ->
//                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
//                        }
//                        .onSuccess { matchingProperties ->
//                            _leads.update { matchingProperties }
//                            _isLoading.update { false }
//                        }
//                }
//        }
//    }

    fun onOrdenationTypeChange(ordenationType: LeadTenantOrdenationType) {
        viewModelScope.launch {
            tenantRepository.updatePreferredLeadsOrdenationTypeId(ordenationType.id)

            watchLeads()
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteLead(lead: Tenant?) {
        _leadToDelete.update { lead }
    }

    fun deleteLead() {
        viewModelScope.launch {
            val leadToDelete = leadToDelete.value ?: return@launch

            tenantRepository.delete(leadToDelete.id!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            val messageId = if (leadToDelete.isLead) {
                R.string.snackbar_delete_lead_succeeded
            } else R.string.snackbar_delete_tenant_succeeded

            _uiEvents.send(UiEvents.LeadDeleteSuccess(messageId = messageId))

            onDeleteLead(lead = null)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data class LeadDeleteSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
