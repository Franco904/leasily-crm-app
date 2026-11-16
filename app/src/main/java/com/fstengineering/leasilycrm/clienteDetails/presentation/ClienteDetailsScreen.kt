package com.fstengineering.leasilycrm.clienteDetails.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsViewModel.UiEvent.NavigateUp
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsViewModel.UiEvent.ShowAssistanceDeleteSnackbar
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsViewModel.UiEvent.ShowClienteDeleteSnackbar
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsViewModel.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsCurrentAssistancePage
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsFloatingActionButton
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsHistoryPage
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsIdentificationPage
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsTabRow
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.ClienteDetailsTopAppBar
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AssistanceInteractionUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AssistanceUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsTab
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceInteractionDetailsBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.cards.AssistanceInteractionCard
import com.fstengineering.leasilycrm.core.presentation.composables.cards.ImovelCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteAssistanceDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteHospedeDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteLeadDialog
import com.fstengineering.leasilycrm.core.presentation.models.AssistanceInteractionBottomSheetUiState
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetailsScreen(
    viewModel: ClienteDetailsViewModel,
    onEditCliente: () -> Unit,
    onRegisterAssistance: (Long?) -> Unit,
    onNewStay: () -> Unit,
    onViewStay: (Long) -> Unit,
    onViewSentImovelDetails: (Long) -> Unit,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()

    val currentMonth = remember { AppDate.now().month }

    var selectedTabId by rememberSaveable {
        mutableIntStateOf(ClienteDetailsTab.Details.id)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loaded = uiState as? ClienteDetailsUiState.Loaded

    var interactionToView by remember { mutableStateOf<AssistanceInteractionUiState?>(null) }
    var pastAssistanceToView by remember { mutableStateOf<AssistanceUiState?>(null) }

    var isViewingAllSentImoveis by rememberSaveable { mutableStateOf(false) }
    var isViewingAllInterations by rememberSaveable { mutableStateOf(false) }

    var mustShowConfirmDeleteClienteDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmDeleteAssistanceDialog by rememberSaveable { mutableStateOf(false) }

    var isBringIntoViewEnabled by rememberSaveable { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
            when (uiEvent) {
                is ShowErrorBottomSheet -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is NavigateUp -> {
                    onBack()
                }

                is ShowClienteDeleteSnackbar -> {
                    val deleteMessageId = when {
                        uiEvent.isLead -> R.string.delete_lead_success_message
                        else -> R.string.delete_tenant_success_message
                    }

                    onUiEvent(UiEvent.ShowSnackBar(deleteMessageId))
                }

                is ShowAssistanceDeleteSnackbar -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_assistance_success_message))
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
        when {
            pastAssistanceToView != null -> pastAssistanceToView = null
            isViewingAllSentImoveis || isViewingAllInterations -> {
                isViewingAllSentImoveis = false
                isViewingAllInterations = false
                pastAssistanceToView = null

                isBringIntoViewEnabled = true
            }

            else -> onBack()
        }
    }

    Scaffold(
        topBar = {
            ClienteDetailsTopAppBar(
                titleText = when {
                    isViewingAllSentImoveis -> stringResource(R.string.lead_tenant_details_top_app_bar_title_all_sent_properties)
                    isViewingAllInterations -> stringResource(R.string.lead_tenant_details_top_app_bar_title_all_interations)
                    pastAssistanceToView != null -> stringResource(R.string.lead_tenant_details_top_app_bar_title_past_assistance)
                    loaded?.isLead == true -> stringResource(R.string.lead_tenant_details_top_app_bar_title_lead)
                    else -> stringResource(R.string.lead_tenant_details_top_app_bar_title_tenant)
                },
                hasMoreOptions = !(isViewingAllSentImoveis || isViewingAllInterations || pastAssistanceToView != null),
                onBack = when {
                    pastAssistanceToView != null -> {
                        { pastAssistanceToView = null }
                    }

                    isViewingAllSentImoveis || isViewingAllInterations -> {
                        {
                            isViewingAllSentImoveis = false
                            isViewingAllInterations = false

                            isBringIntoViewEnabled = true
                        }
                    }

                    else -> onBack
                },
                onEditCliente = onEditCliente,
                onDeleteCliente = { mustShowConfirmDeleteClienteDialog = true },
            )
        },
        floatingActionButton = {
            if (loaded != null) {
                ClienteDetailsFloatingActionButton(
                    hasCurrentStay = loaded.currentStayId != null,
                    onClick = {
                        if (loaded.currentStayId != null) onViewStay(loaded.currentStayId)
                        else onNewStay()
                    },
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState is ClienteDetailsUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            loaded != null && (isViewingAllSentImoveis || isViewingAllInterations) -> {
                val currentAssistance = loaded.currentAssistance

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    when {
                        isViewingAllSentImoveis -> {
                            val sortedSentImoveis = currentAssistance?.sentImoveis
                                ?: emptyList()

                            itemsIndexed(
                                items = sortedSentImoveis,
                                key = { _, entry -> entry.id },
                                contentType = { _, entryType -> entryType },
                            ) { i, selectedImovel ->
                                if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                                ImovelCard(
                                    imovel = selectedImovel,
                                    onCardClick = { onViewSentImovelDetails(selectedImovel.id) },
                                    hasMoreOptionsMenu = false,
                                    hasCharacteristicsChipsRow = false,
                                )
                            }
                        }

                        isViewingAllInterations -> {
                            val interactions = currentAssistance?.interactionsByMonth
                                ?: emptyMap()

                            itemsIndexed(
                                items = interactions,
                                key = { _, (monthYear, _) -> monthYear.id },
                                contentType = { _, entryType -> entryType },
                            ) { i, (monthYear, interactions) ->
                                val monthLabel = when {
                                    monthYear.month == currentMonth -> stringResource(R.string.this_month_label)
                                    else -> context
                                        .getMonthLabelByNumber(monthYear.month)
                                        .lowercase()
                                        .capitalizeFirst()
                                }

                                if (i > 0) Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = monthLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    interactions.forEach { interaction ->
                                        AssistanceInteractionCard(
                                            interaction = interaction.toCardUiState(),
                                            onViewInteractionDetails = {
                                                interactionToView = interaction
                                            },
                                            hasMoreOptionsMenu = false,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            loaded != null && pastAssistanceToView != null -> {
                ClienteDetailsCurrentAssistancePage(
                    currentAssistance = pastAssistanceToView,
                    onRegisterAssistance = { onRegisterAssistance(null) },
                    onEditAssistance = { onRegisterAssistance(loaded.currentAssistance?.id) },
                    onDeleteAssistance = { mustShowConfirmDeleteAssistanceDialog = true },
                    onViewAllSentImoveis = { isViewingAllSentImoveis = true },
                    onViewAllInteractions = { isViewingAllInterations = true },
                    onViewSentImovelDetails = { imovelId ->
                        onViewSentImovelDetails(imovelId)
                    },
                    onViewInteractionDetails = { interaction ->
                        interactionToView = interaction
                    },
                    isViewingAllSentImoveis = isViewingAllSentImoveis,
                    isViewingAllInterations = isViewingAllInterations,
                    isBringIntoViewEnabled = isBringIntoViewEnabled,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }

            loaded != null -> {
                Column {
                    AnimatedVisibility(
                        visible = !(isViewingAllSentImoveis || isViewingAllInterations),
                    ) {
                        ClienteDetailsTabRow(
                            selectedTabId = selectedTabId,
                            onTabSelect = { selectedTabId = it },
                            hasHistoryTab = loaded.isOld,
                            modifier = Modifier
                                .padding(innerPadding)
                        )
                    }
                    AnimatedContent(
                        targetState = ClienteDetailsTab.fromId(selectedTabId),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) { selectedTab ->
                        when (selectedTab) {
                            ClienteDetailsTab.Details -> {
                                ClienteDetailsIdentificationPage(
                                    tag = loaded.tag,
                                    name = loaded.name,
                                    city = loaded.city,
                                    identityDocument = loaded.identityDocument,
                                    phone = loaded.phone,
                                    email = loaded.email,
                                    contactOrigin = loaded.contactOrigin,
                                    observations = loaded.observations,
                                    isLead = loaded.isLead,
                                )
                            }

                            ClienteDetailsTab.CurrentAssistance -> {
                                ClienteDetailsCurrentAssistancePage(
                                    currentAssistance = loaded.currentAssistance,
                                    onRegisterAssistance = { onRegisterAssistance(null) },
                                    onEditAssistance = { onRegisterAssistance(loaded.currentAssistance?.id) },
                                    onDeleteAssistance = {
                                        mustShowConfirmDeleteAssistanceDialog = true
                                    },
                                    onViewAllSentImoveis = { isViewingAllSentImoveis = true },
                                    onViewAllInteractions = { isViewingAllInterations = true },
                                    onViewSentImovelDetails = { imovelId ->
                                        onViewSentImovelDetails(imovelId)
                                    },
                                    onViewInteractionDetails = { interaction ->
                                        interactionToView = interaction
                                    },
                                    isViewingAllSentImoveis = isViewingAllSentImoveis,
                                    isViewingAllInterations = isViewingAllInterations,
                                    isBringIntoViewEnabled = isBringIntoViewEnabled,
                                )
                            }

                            ClienteDetailsTab.History -> {
                                ClienteDetailsHistoryPage(
                                    historyByYear = loaded.historyByYear,
                                    onViewHistoryItem = { id, isAssistance ->
                                        when {
                                            isAssistance -> {
                                                pastAssistanceToView = loaded.pastAssistances
                                                    .find { it.id == id }
                                            }

                                            else -> onViewStay(id)
                                        }
                                    }
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        if (interactionToView != null) {
            AssistanceInteractionDetailsBottomSheet(
                sheetState = sheetState,
                interaction = AssistanceInteractionBottomSheetUiState(
                    date = interactionToView!!.date,
                    title = interactionToView!!.title,
                    description = interactionToView!!.description,
                ),
                onDismissRequest = { interactionToView = null },
            )
        }

        if (mustShowConfirmDeleteClienteDialog && loaded != null) {
            if (loaded.isLead) {
                ConfirmDeleteLeadDialog(
                    onConfirmDelete = {
                        viewModel.onDeleteCliente()

                        mustShowConfirmDeleteClienteDialog = false
                        onBack()
                    },
                    onCancel = { mustShowConfirmDeleteClienteDialog = false },
                )
            } else {
                ConfirmDeleteHospedeDialog(
                    onConfirmDelete = {
                        viewModel.onDeleteCliente()

                        mustShowConfirmDeleteClienteDialog = false
                        onBack()
                    },
                    onCancel = { mustShowConfirmDeleteClienteDialog = false },
                )
            }
        }

        if (mustShowConfirmDeleteAssistanceDialog) {
            ConfirmDeleteAssistanceDialog(
                onConfirmDelete = {
                    val assistanceId =
                        loaded?.currentAssistance?.id ?: return@ConfirmDeleteAssistanceDialog
                    viewModel.deleteCurrentAssistance(assistanceId)

                    mustShowConfirmDeleteAssistanceDialog = false
                },
                onCancel = { mustShowConfirmDeleteAssistanceDialog = false },
            )
        }
    }
}
