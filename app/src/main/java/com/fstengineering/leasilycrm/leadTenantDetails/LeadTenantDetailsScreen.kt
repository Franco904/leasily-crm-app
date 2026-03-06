package com.fstengineering.leasilycrm.leadTenantDetails

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceInteractionDetailsBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.cards.AssistanceInteractionCard
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteAssistanceDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteLeadDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteTenantDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel.UiEvents.CurrentAssistanceDeleteSuccess
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel.UiEvents.LeadTenantDeleteSuccess
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel.UiEvents.NavigateUp
import com.fstengineering.leasilycrm.leadTenantDetails.composables.LeadTenantDetailsCurrentAssistancePage
import com.fstengineering.leasilycrm.leadTenantDetails.composables.LeadTenantDetailsHistoryPage
import com.fstengineering.leasilycrm.leadTenantDetails.composables.LeadTenantDetailsIdentificationPage
import com.fstengineering.leasilycrm.leadTenantDetails.composables.LeadTenantDetailsTabRow
import com.fstengineering.leasilycrm.leadTenantDetails.composables.LeadTenantDetailsTopAppBar
import com.fstengineering.leasilycrm.leadTenantDetails.models.LeadTenantDetailsTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsScreen(
    leadTenantId: Long,
    viewModel: LeadTenantDetailsViewModel,
    onEditLeadTenant: () -> Unit,
    onRegisterAssistance: (Long?) -> Unit,
    onNewStay: () -> Unit,
    onViewStay: (Long) -> Unit,
    onViewSentPropertyDetails: (Long) -> Unit,
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
        mutableIntStateOf(LeadTenantDetailsTab.Details.id)
    }

    val leadTenant by viewModel.leadTenant.collectAsStateWithLifecycle()
    val assistancePropertyCharacteristics by viewModel.assistancePropertyCharacteristics.collectAsStateWithLifecycle()

    val currentAssistance = remember(leadTenant.assistances) {
        leadTenant.assistances.find { !it.isFinished }
    }
    val currentStay = remember(leadTenant.stays) {
        leadTenant.stays.find { !it.isCheckOutDone }
    }

    val sortedSentProperties = remember(currentAssistance?.sentProperties) {
        currentAssistance?.sentProperties
            ?.sortedByDescending { it.updateDateTime?.dateTime() }
            ?.map { it.property }
            ?: persistentListOf()
    }

    val sortedInteractions = remember(currentAssistance?.interactions) {
        currentAssistance?.interactions
            ?.sortedByDescending { it.date?.date() }
            ?.toPersistentList()
            ?: persistentListOf()
    }
    var interactionToView by remember { mutableStateOf<AssistanceInteraction?>(null) }

    var pastAssistanceToView by remember { mutableStateOf<Assistance?>(null) }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var isViewingAllSentProperties by rememberSaveable { mutableStateOf(false) }
    var isViewingAllInterations by rememberSaveable { mutableStateOf(false) }

    var mustShowConfirmDeleteLeadTenantDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmDeleteAssistanceDialog by rememberSaveable { mutableStateOf(false) }

    var isBringIntoViewEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyId = leadTenantId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is NavigateUp -> {
                    onBack()
                }

                is LeadTenantDeleteSuccess -> {
                    val deleteMessageId = when {
                        leadTenant.isLead -> R.string.delete_lead_success_message
                        else -> R.string.delete_tenant_success_message
                    }

                    onUiEvent(UiEvent.ShowSnackBar(deleteMessageId))
                }

                is CurrentAssistanceDeleteSuccess -> {
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
            isViewingAllSentProperties || isViewingAllInterations -> {
                isViewingAllSentProperties = false
                isViewingAllInterations = false
                pastAssistanceToView = null

                isBringIntoViewEnabled = true
            }

            else -> onBack()
        }
    }

    Scaffold(
        topBar = {
            LeadTenantDetailsTopAppBar(
                titleText = when {
                    isViewingAllSentProperties -> stringResource(R.string.lead_tenant_details_top_app_bar_title_all_sent_properties)
                    isViewingAllInterations -> stringResource(R.string.lead_tenant_details_top_app_bar_title_all_interations)
                    pastAssistanceToView != null -> stringResource(R.string.lead_tenant_details_top_app_bar_title_past_assistance)
                    leadTenant.isLead -> stringResource(R.string.lead_tenant_details_top_app_bar_title_lead)
                    else -> stringResource(R.string.lead_tenant_details_top_app_bar_title_tenant)
                },
                hasMoreOptions = !(isViewingAllSentProperties || isViewingAllInterations || pastAssistanceToView != null),
                onBack = when {
                    pastAssistanceToView != null -> {
                        { pastAssistanceToView = null }
                    }

                    isViewingAllSentProperties || isViewingAllInterations -> {
                        {
                            isViewingAllSentProperties = false
                            isViewingAllInterations = false

                            isBringIntoViewEnabled = true
                        }
                    }

                    else -> onBack
                },
                onEditLeadTenant = onEditLeadTenant,
                onDeleteLeadTenant = { mustShowConfirmDeleteLeadTenantDialog = true },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { if (currentStay != null) onViewStay(currentStay.id!!) else onNewStay() },
            ) {
                if (currentStay != null) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_view),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.view_property_stay_action_button_text),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.calendar_add_on),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.new_property_stay_action_button_text),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            isLoading -> {
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
            }

            isViewingAllSentProperties || isViewingAllInterations -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    when {
                        isViewingAllSentProperties -> {
                            itemsIndexed(
                                items = sortedSentProperties,
                                key = { i, entry -> entry?.id ?: "${entry?.updateDateTime}-$i" },
                                contentType = { _, entryType -> entryType },
                            ) { i, selectedProperty ->
                                requireNotNull(selectedProperty)

                                if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                                PropertyCard(
                                    propertyDetails = selectedProperty,
                                    onCardClick = { onViewSentPropertyDetails(selectedProperty.id!!) },
                                    hasMoreOptionsMenu = false,
                                    hasCharacteristicsChipsRow = false,
                                )
                            }
                        }

                        isViewingAllInterations -> {
                            itemsIndexed(
                                items = sortedInteractions.groupBy { interaction ->
                                    CalendarMonthYear(
                                        month = interaction.date?.month ?: 0,
                                        year = interaction.date?.year ?: 0,
                                    )
                                },
                                key = { i, (monthYear, _) -> monthYear.id },
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
                                            interaction = interaction,
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

            pastAssistanceToView != null -> {
                LeadTenantDetailsCurrentAssistancePage(
                    currentAssistance = pastAssistanceToView,
                    assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                    lastSentProperty = sortedSentProperties.firstOrNull(),
                    lastInteraction = sortedInteractions.firstOrNull(),
                    onRegisterAssistance = { onRegisterAssistance(null) },
                    onEditAssistance = { onRegisterAssistance(currentAssistance?.id!!) },
                    onDeleteAssistance = { mustShowConfirmDeleteAssistanceDialog = true },
                    onViewAllSentProperties = { isViewingAllSentProperties = true },
                    onViewAllInteractions = { isViewingAllInterations = true },
                    onViewSentPropertyDetails = { propertyId ->
                        onViewSentPropertyDetails(propertyId)
                    },
                    onViewInteractionDetails = { interaction ->
                        interactionToView = interaction
                    },
                    isViewingAllSentProperties = isViewingAllSentProperties,
                    isViewingAllInterations = isViewingAllInterations,
                    isBringIntoViewEnabled = isBringIntoViewEnabled,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }

            else -> {
                Column {
                    AnimatedVisibility(
                        visible = !(isViewingAllSentProperties || isViewingAllInterations),
                    ) {
                        LeadTenantDetailsTabRow(
                            selectedTabId = selectedTabId,
                            onTabSelect = { selectedTabId = it },
                            hasHistoryTab = leadTenant.isOld,
                            modifier = Modifier
                                .padding(innerPadding)
                        )
                    }
                    AnimatedContent(
                        targetState = LeadTenantDetailsTab.fromId(selectedTabId),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) { selectedTab ->
                        when (selectedTab) {
                            LeadTenantDetailsTab.Details -> {
                                LeadTenantDetailsIdentificationPage(leadTenant = leadTenant)
                            }

                            LeadTenantDetailsTab.CurrentAssistance -> {
                                LeadTenantDetailsCurrentAssistancePage(
                                    currentAssistance = currentAssistance,
                                    assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                                    lastSentProperty = sortedSentProperties.firstOrNull(),
                                    lastInteraction = sortedInteractions.firstOrNull(),
                                    onRegisterAssistance = { onRegisterAssistance(null) },
                                    onEditAssistance = { onRegisterAssistance(currentAssistance?.id!!) },
                                    onDeleteAssistance = {
                                        mustShowConfirmDeleteAssistanceDialog = true
                                    },
                                    onViewAllSentProperties = { isViewingAllSentProperties = true },
                                    onViewAllInteractions = { isViewingAllInterations = true },
                                    onViewSentPropertyDetails = { propertyId ->
                                        onViewSentPropertyDetails(propertyId)
                                    },
                                    onViewInteractionDetails = { interaction ->
                                        interactionToView = interaction
                                    },
                                    isViewingAllSentProperties = isViewingAllSentProperties,
                                    isViewingAllInterations = isViewingAllInterations,
                                    isBringIntoViewEnabled = isBringIntoViewEnabled,
                                )
                            }

                            LeadTenantDetailsTab.History -> {
                                LeadTenantDetailsHistoryPage(
                                    leadTenant = leadTenant,
                                    onViewHistoryItem = { id, isAssistance ->
                                        when {
                                            isAssistance -> {
                                                pastAssistanceToView = leadTenant.assistances
                                                    .find { it.id == id }
                                            }

                                            else -> onViewStay(id)
                                        }
                                    }
                                )
                            }

                            null -> null
                        }
                    }
                }
            }
        }
        if (interactionToView != null) {
            AssistanceInteractionDetailsBottomSheet(
                sheetState = sheetState,
                interaction = interactionToView!!,
                onDismissRequest = { interactionToView = null },
            )
        }
        if (mustShowConfirmDeleteLeadTenantDialog) {
            if (leadTenant.isLead) {
                ConfirmDeleteLeadDialog(
                    onConfirmDelete = {
                        viewModel.deleteLeadTenant()

                        mustShowConfirmDeleteLeadTenantDialog = false
                        onBack()
                    },
                    onCancel = { mustShowConfirmDeleteLeadTenantDialog = false },
                )
            } else {
                ConfirmDeleteTenantDialog(
                    onConfirmDelete = {
                        viewModel.deleteLeadTenant()

                        mustShowConfirmDeleteLeadTenantDialog = false
                        onBack()
                    },
                    onCancel = { mustShowConfirmDeleteLeadTenantDialog = false },
                )
            }
        }
        if (mustShowConfirmDeleteAssistanceDialog) {
            ConfirmDeleteAssistanceDialog(
                onConfirmDelete = {
                    viewModel.deleteCurrentAssistance(
                        currentAssistance = currentAssistance!!,
                    )

                    mustShowConfirmDeleteAssistanceDialog = false
                },
                onCancel = { mustShowConfirmDeleteAssistanceDialog = false },
            )
        }
    }
}
