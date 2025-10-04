package com.fstengineering.leasilycrm.home.stays

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteStayDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmRegisterStayCheckOutDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.StayTypeDialog
import com.fstengineering.leasilycrm.core.presentation.composables.rememberHorizontalCenterScroller
import com.fstengineering.leasilycrm.core.presentation.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.home.stays.StaysViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.stays.StaysViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.stays.StaysViewModel.UiEvents.StayDeleteSuccess
import com.fstengineering.leasilycrm.home.stays.composables.StayCard
import com.fstengineering.leasilycrm.home.stays.composables.StayFilterButtonGroup
import com.fstengineering.leasilycrm.home.stays.composables.StaysEmptySection
import com.fstengineering.leasilycrm.home.stays.models.StayFilterType
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun StaysScreen(
    viewModel: StaysViewModel,
    onRegisterStayBlank: (Long?, Boolean) -> Unit,
    onViewStayDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
    showActionSnackbar: (String, String, () -> Unit) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val horizontalScrollState = rememberScrollState()
    val centerHorizontalScrollState = rememberHorizontalCenterScroller(horizontalScrollState)
    val lazyListState = rememberLazyListState()

    var scrollContainerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var buttonsCoords by remember {
        mutableStateOf<PersistentMap<StayFilterType, LayoutCoordinates?>>(
            StayFilterType.entries
                .associateWith { null }
                .toPersistentMap(),
        )
    }

    val currentMonth = remember { LocalDate.now().monthValue }
    val currentYear = remember { LocalDate.now().year }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val currentStayFilterType by viewModel.currentStayFilterType.collectAsStateWithLifecycle()

    val stays by viewModel.stays.collectAsStateWithLifecycle()
    val stayToDelete by viewModel.stayToDelete.collectAsStateWithLifecycle()

    val listStatus = remember(isLoading, stays) {
        when {
            isLoading -> ListState.Loading
            stays.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }
    }

    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmRegisterStayCheckOutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is StayDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_stay_succeeded))
                }

                StaysViewModel.UiEvents.RegisterCheckInSuccess -> {
                    showActionSnackbar(
                        context.getString(R.string.snackbar_register_check_in_stay_succeeded),
                        context.getString(R.string.undo_button_text),
                        viewModel::undoRegisterStayCheckIn,
                    )
                }

                StaysViewModel.UiEvents.RegisterCheckOutSuccess -> {
                    showActionSnackbar(
                        context.getString(R.string.snackbar_register_check_out_stay_succeeded),
                        context.getString(R.string.undo_button_text),
                        viewModel::undoRegisterStayCheckOut,
                    )
                }
            }
        }
    }

    LaunchedEffect(currentStayFilterType, buttonsCoords) {
        val currentStayFilterTypeButtonCoords =
            buttonsCoords[currentStayFilterType] ?: return@LaunchedEffect

        coroutineScope.launch {
            centerHorizontalScrollState(
                scrollContainerCoords,
                currentStayFilterTypeButtonCoords,
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowStayTypeDialog = true },
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
            StayFilterButtonGroup(
                selectedFilterType = currentStayFilterType,
                onFilterTypeSelect = viewModel::onStayFilterTypeChange,
                buttonsCoords = buttonsCoords,
                onButtonGroupCoords = { coords -> buttonsCoords = coords },
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
                        StaysEmptySection(stayFilterType = currentStayFilterType)
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
                                items = stays.groupBy { stay ->
                                    CalendarMonthYear(
                                        id = stay.checkInDateTime!!.monthValue + stay.checkInDateTime.year,
                                        month = stay.checkInDateTime.monthValue,
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
                                        StayCard(
                                            stay = stay,
                                            onCardClick = { onViewStayDetails(stay.id!!) },
                                            onEditStay = {
                                                onRegisterStayBlank(
                                                    stay.id!!,
                                                    stay.isFromOtherRealtor,
                                                )
                                            },
                                            onDeleteStay = { viewModel.onDeleteStay(stay) },
                                            onRegisterCheckIn = { viewModel.registerStayCheckIn(stay) },
                                            onRegisterCheckOut = {
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
        if (stayToDelete != null) {
            ConfirmDeleteStayDialog(
                onConfirmDelete = viewModel::deleteStay,
                onCancel = { viewModel.onDeleteStay(stay = null) },
            )
        }
        if (mustShowStayTypeDialog) {
            StayTypeDialog(
                onLoggedRealtorStayType = {
                    onRegisterStayBlank(null, false)
                    mustShowStayTypeDialog = false
                },
                onOtherRealtorStayType = {
                    onRegisterStayBlank(null, true)
                    mustShowStayTypeDialog = false
                },
                onCancel = { mustShowStayTypeDialog = false }
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
