package com.fstengineering.leasilycrm.clientes.presentation.hospedes

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.clientes.presentation.models.HospedeFilterType
import com.fstengineering.leasilycrm.clientes.presentation.hospedes.composables.HospedeFilterButtonGroup
import com.fstengineering.leasilycrm.core.presentation.composables.HospedesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByMenuOption
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.OrderByTextButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.ClienteCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteHospedeDialog
import com.fstengineering.leasilycrm.core.presentation.models.ClienteOrderType
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HospedesPage(
    viewModel: HospedesViewModel,
    isSearchingHospede: Boolean,
    searchInput: String?,
    onRegisterHospede: (Long?) -> Unit,
    onViewHospedeDetails: (Long) -> Unit,
    onNewAtendimento: (Long) -> Unit,
    onNewStay: (Long) -> Unit,
    onViewStay: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val lazyListState = rememberLazyListState()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val currentHospedeFilter by viewModel.currentHospedeFilterType.collectAsStateWithLifecycle()
    val orderType by viewModel.orderType.collectAsStateWithLifecycle()

    val hospedes by viewModel.hospedes.collectAsStateWithLifecycle()
    val hospedeToDelete by viewModel.hospedeToDelete.collectAsStateWithLifecycle()

    val listStatus by remember(isLoading, hospedes) {
        val status = when {
            isLoading -> ListState.Loading
            hospedes.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }

        mutableStateOf(status)
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycleOwner.lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycleOwner.lifecycle) { uiEvent ->
            when (uiEvent) {
                is HospedesViewModel.UiEvent.ShowErrorBottomSheet -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is HospedesViewModel.UiEvent.ShowDeleteSuccessSnackbar -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_tenant_success_message))
                }
            }
        }
    }

    LaunchedEffect(searchInput) {
        viewModel.onSearchInputChange(searchInput = searchInput)
    }

    LaunchedEffect(isSearchingHospede) {
        val filterType =
            if (isSearchingHospede) null else viewModel.hospedeFilterTypeBeforeSearch
        viewModel.onHospedeFilterTypeChange(hospedeFilterType = filterType)
    }

    Column {
        if (!isSearchingHospede && currentHospedeFilter != null) {
            Spacer(modifier = Modifier.height(16.dp))
            HospedeFilterButtonGroup(
                selectedFilterType = currentHospedeFilter!!,
                onFilterTypeSelect = viewModel::onHospedeFilterTypeChange,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
        AnimatedContent(
            targetState = listStatus,
            modifier = modifier
                .fillMaxSize()
        ) { listStatus ->
            when (listStatus) {
                ListState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        AppCircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }

                ListState.Empty -> {
                    HospedesEmptySection(
                        messageId = when {
                            isSearchingHospede -> R.string.tenants_empty_search_text
                            currentHospedeFilter == HospedeFilterType.ComEstadia -> R.string.tenants_upcoming_stay_empty_text
                            currentHospedeFilter == HospedeFilterType.Antigo -> R.string.tenants_old_empty_text
                            else -> R.string.tenants_generic_empty_text
                        },
                    )
                }

                ListState.Idle -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        if (!isSearchingHospede) {
                            item {
                                OrderByTextButton(
                                    orderType = orderType,
                                    orderByMenuOptions = persistentListOf(
                                        OrderByMenuOption(
                                            text = stringResource(R.string.order_by_name_text),
                                            onClick = {
                                                viewModel.onOrderTypeChange(
                                                    ClienteOrderType.Name,
                                                )
                                            },
                                        ),
                                        OrderByMenuOption(
                                            text = stringResource(R.string.order_by_last_update_date_text),
                                            onClick = {
                                                viewModel.onOrderTypeChange(
                                                    ClienteOrderType.LastUpdateDate,
                                                )
                                            },
                                        ),
                                    ),
                                )
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                        itemsIndexed(
                            items = hospedes,
                            key = { _, item -> item.cliente.id },
                            contentType = { _, entryType -> entryType },
                        ) { _, item ->
                            ClienteCard(
                                cliente = item.cliente,
                                onCardClick = { onViewHospedeDetails(item.cliente.id) },
                                onEditCliente = { onRegisterHospede(item.cliente.id) },
                                onDeleteCliente = { viewModel.onDeleteHospede(item.cliente.id) },
                                onNewAtendimento = { onNewAtendimento(item.cliente.id) },
                                onViewStay = { item.currentStayId?.let { id -> onViewStay(id) } },
                                onNewStay = { onNewStay(item.cliente.id) },
                                hasMoreOptionsMenu = true,
                                hasAtendimentoText = true,
                                hasPrimaryActions = true,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }
    if (hospedeToDelete != null) {
        ConfirmDeleteHospedeDialog(
            onConfirmDelete = viewModel::deleteHospede,
            onCancel = { viewModel.onDeleteHospede(null) },
        )
    }
}
