package com.fstengineering.leasilycrm.occupiedPeriods.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.OccupiedPeriodsEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteStayDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmRegisterStayCheckOutDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.OccupiedPeriodTypeDialog
import com.fstengineering.leasilycrm.core.presentation.composables.rememberHorizontalCenterScroller
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.models.OccupiedPeriodFilterType
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel.UiEvent.OccupiedPeriodDeleteSuccess
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel.UiEvent.RegisterCheckInSuccess
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel.UiEvent.RegisterCheckOutSuccess
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.composables.OccupiedPeriodCard
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.composables.OccupiedPeriodFilterButtonGroup
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch

@Composable
fun OccupiedPeriodsPage(
    viewModel: OccupiedPeriodsViewModel,
    onRegisterOccupiedPeriod: (Long?, Boolean) -> Unit,
    onViewOccupiedPeriodDetails: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = rememberCoroutineScope()
    val horizontalScrollState = rememberScrollState()
    val centerHorizontalScrollState = rememberHorizontalCenterScroller(horizontalScrollState)
    val lazyListState = rememberLazyListState()

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var buttonsCoords by remember {
        mutableStateOf<PersistentMap<OccupiedPeriodFilterType, LayoutCoordinates?>>(
            OccupiedPeriodFilterType.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val currentMonth = remember { AppDate.now().month }
    val currentYear = remember { AppDate.now().year }

    val occupiedPeriods by viewModel.occupiedPeriods.collectAsStateWithLifecycle()
    val occupiedPeriodToDelete by viewModel.occupiedPeriodToDelete.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val currentOccupiedPeriodFilterType by viewModel.currentOccupiedPeriodFilterType.collectAsStateWithLifecycle()

    val staysAbleToCheckInCount by viewModel.staysAbleToCheckInCount.collectAsStateWithLifecycle()
    val staysAbleToCheckOutCount by viewModel.staysAbleToCheckOutCount.collectAsStateWithLifecycle()

    val listStatus = remember(isLoading, occupiedPeriods) {
        when {
            isLoading -> ListState.Loading
            occupiedPeriods.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }
    }

    var mustShowOccupiedPeriodTypeDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmRegisterStayCheckOutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is OccupiedPeriodDeleteSuccess -> {
                    if (occupiedPeriodToDelete == null) return@collect

                    val messageId =
                        if (occupiedPeriodToDelete!!.isUnavailablePeriod) R.string.delete_unavailable_period_success_message
                        else R.string.delete_stay_success_message

                    onUiEvent(UiEvent.ShowSnackBar(messageId))
                    viewModel.onDeleteStay(stay = null)
                }

                RegisterCheckInSuccess -> {
                    onUiEvent(
                        UiEvent.ShowActionSnackBar(
                            R.string.register_check_in_stay_success_message,
                            R.string.undo_button_text,
                            viewModel::undoRegisterStayCheckIn,
                        )
                    )
                }

                RegisterCheckOutSuccess -> {
                    onUiEvent(
                        UiEvent.ShowActionSnackBar(
                            R.string.register_check_out_stay_success_message,
                            R.string.undo_button_text,
                            viewModel::undoRegisterStayCheckOut,
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(currentOccupiedPeriodFilterType, buttonsCoords) {
        val filterTypeButtonCoords =
            buttonsCoords[currentOccupiedPeriodFilterType] ?: return@LaunchedEffect

        coroutineScope.launch {
            centerHorizontalScrollState(
                scrollContainerCoords,
                filterTypeButtonCoords,
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.onPause()
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowOccupiedPeriodTypeDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OccupiedPeriodFilterButtonGroup(
                selectedFilterType = currentOccupiedPeriodFilterType,
                onFilterTypeSelect = viewModel::onStayFilterTypeChange,
                buttonsCoords = buttonsCoords,
                onButtonGroupCoords = { coords -> buttonsCoords = coords },
                staysAbleToCheckInCount = staysAbleToCheckInCount,
                staysAbleToCheckOutCount = staysAbleToCheckOutCount,
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .onGloballyPositioned { coords -> scrollContainerCoords = coords }
            )
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
                        OccupiedPeriodsEmptySection(occupiedPeriodFilterType = currentOccupiedPeriodFilterType)
                    }

                    ListState.Idle -> {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            itemsIndexed(
                                items = occupiedPeriods.groupBy { stay ->
                                    CalendarMonthYear(
                                        month = stay.checkInDateTime.month,
                                        year = stay.checkInDateTime.year,
                                    )
                                },
                                key = { i, (monthYear, _) -> monthYear.id },
                                contentType = { _, entryType -> entryType },
                            ) { i, (monthYear, stays) ->
                                val monthLabel = when {
                                    monthYear.month == currentMonth -> stringResource(R.string.this_month_label)
                                    else -> context
                                        .getMonthLabelByNumber(monthYear.month)
                                        .lowercase()
                                        .capitalizeFirst()
                                }

                                val isCurrentYear = monthYear.year == currentYear

                                if (i > 0) Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "$monthLabel ${if (!isCurrentYear) monthYear.year else ""}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Column {
                                    stays.forEach { stay ->
                                        OccupiedPeriodCard(
                                            occupiedPeriod = stay,
                                            onCardClick = { onViewOccupiedPeriodDetails(stay.id) },
                                            onEditPeriod = {
                                                onRegisterOccupiedPeriod(
                                                    stay.id,
                                                    stay.isUnavailablePeriod,
                                                )
                                            },
                                            onDeletePeriod = { viewModel.onDeleteStay(stay) },
                                            onRegisterStayCheckIn = {
                                                viewModel.registerStayCheckIn(
                                                    stay
                                                )
                                            },
                                            onRegisterStayCheckOut = {
                                                viewModel.onRegisterStayCheckOut(stay)

                                                if (viewModel.mustNotShowCheckOutDialog) {
                                                    viewModel.registerStayCheckOut()
                                                } else {
                                                    mustShowConfirmRegisterStayCheckOutDialog = true
                                                }
                                            },
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
            }
        }
        if (occupiedPeriodToDelete != null) {
            ConfirmDeleteStayDialog(
                isUnavailablePeriod = occupiedPeriodToDelete!!.isUnavailablePeriod,
                onConfirmDelete = viewModel::deleteStay,
                onCancel = { viewModel.onDeleteStay(stay = null) },
            )
        }
        if (mustShowOccupiedPeriodTypeDialog) {
            OccupiedPeriodTypeDialog(
                onTenantStayType = {
                    onRegisterOccupiedPeriod(null, false)
                    mustShowOccupiedPeriodTypeDialog = false
                },
                onUnavailablePeriodStayType = {
                    onRegisterOccupiedPeriod(null, true)
                    mustShowOccupiedPeriodTypeDialog = false
                },
                onCancel = { mustShowOccupiedPeriodTypeDialog = false }
            )
        }
        if (mustShowConfirmRegisterStayCheckOutDialog) {
            ConfirmRegisterStayCheckOutDialog(
                onConfirmCheckOut = { mustNotShowCheckOutDialogAnymore ->
                    viewModel.registerStayCheckOut(
                        mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
                    )
                    mustShowConfirmRegisterStayCheckOutDialog = false
                },
                onCancel = { mustShowConfirmRegisterStayCheckOutDialog = false },
            )
        }
    }
}
