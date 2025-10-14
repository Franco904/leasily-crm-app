package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.customColors
import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.StaysEmptySection
import com.fstengineering.leasilycrm.core.presentation.models.StayFilterType
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyStaysSection(
    property: Property,
    monthYearOptions: ImmutableList<CalendarMonthYear>,
    selectedYearStaysDates: StaysStructuredDatesMap,
    selectedPeriod: Pair<CalendarMonthYear?, CalendarMonthYear?>,
    onPeriodSelect: (Pair<CalendarMonthYear?, CalendarMonthYear?>) -> Unit,
    onViewStay: (Long) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()

    val startMonthYearValue = remember(selectedPeriod) {
        if (selectedPeriod.first == null) return@remember context.getString(
            R.string.property_stays_start_month_year_filter_field_placeholder,
        )

        val abbr = context.getMonthLabelAbbreviationByNumber(
            monthNumber = selectedPeriod.first?.month ?: 1,
        )
            .lowercase()
            .capitalizeFirst()
        "$abbr ${selectedPeriod.first?.year}"
    }

    val endMonthYearValue = remember(selectedPeriod) {
        if (selectedPeriod.second == null) return@remember context.getString(
            R.string.property_stays_end_month_year_filter_field_placeholder,
        )

        val abbr = context.getMonthLabelAbbreviationByNumber(
            monthNumber = selectedPeriod.second?.month ?: 1,
        )
            .lowercase()
            .capitalizeFirst()
        "$abbr ${selectedPeriod.second?.year}"
    }

    val stayColorToTenantsNames = remember(selectedYearStaysDates) {
        if (selectedYearStaysDates.isEmpty()) return@remember persistentListOf()

        val idsGroup = selectedYearStaysDates.values
            .flatten()
            .groupBy { it.stayId }

        idsGroup
            .filter { (_, stayDates) ->
                val isUnavailablePeriod =
                    stayDates.first().secondHalfPeriodIndex == UNAVAILABLE_PERIOD_INDEX
                !isUnavailablePeriod
            }
            .map { (stayId, stayDates) ->
                val stayTenantColor = stayDates.first().secondHalfPeriodIndex
                    ?.let { i -> @Composable { customColors[i] } }
                    ?: @Composable { Color.Transparent }

                val tenantName = property.stays
                    .find { it.id == stayId }?.tenant?.name
                    ?: context.getString(R.string.unknown_text)

                Triple(stayId, stayTenantColor, tenantName)
            }
            .toPersistentList()
    }

    var mustShowStartMonthYearFilterComboBottomsheet by remember { mutableStateOf(false) }
    var mustShowEndMonthYearFilterComboBottomsheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = isLoading,
        ) { isLoading ->
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppCircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                    )
                }
            } else {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MonthYearFilterField(
                            placeholder = stringResource(R.string.property_stays_start_month_year_filter_field_placeholder),
                            monthYearValue = startMonthYearValue,
                            onTap = { mustShowStartMonthYearFilterComboBottomsheet = true },
                            modifier = Modifier
                                .weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.until_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .offset(y = (-4).dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MonthYearFilterField(
                            placeholder = stringResource(R.string.property_stays_end_month_year_filter_field_placeholder),
                            monthYearValue = endMonthYearValue,
                            onTap = { mustShowEndMonthYearFilterComboBottomsheet = true },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (selectedYearStaysDates.isEmpty()) {
                        StaysEmptySection(stayFilterType = StayFilterType.All)
                    } else {
                        StaysCalendarList(
                            stayColorToTenantsNames = stayColorToTenantsNames,
                            stays = property.stays,
                            selectedYearStaysDates = selectedYearStaysDates,
                            onViewStay = onViewStay,
                        )
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }
        }
    }
    if (mustShowStartMonthYearFilterComboBottomsheet) {
        StartMonthYearFilterComboBottomsheet(
            sheetState = sheetState,
            monthYearOptions = monthYearOptions,
            onMonthYearSelect = { onPeriodSelect(it to selectedPeriod.second) },
            onDismiss = { mustShowStartMonthYearFilterComboBottomsheet = false },
        )
    }
    if (mustShowEndMonthYearFilterComboBottomsheet) {
        EndMonthYearFilterComboBottomsheet(
            sheetState = sheetState,
            monthYearOptions = monthYearOptions,
            onMonthYearSelect = { onPeriodSelect(selectedPeriod.first to it) },
            onDismiss = { mustShowEndMonthYearFilterComboBottomsheet = false },
        )
    }
}
