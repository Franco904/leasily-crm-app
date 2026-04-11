package com.fstengineering.leasilycrm.propertyStays.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.StaysEmptySection
import com.fstengineering.leasilycrm.core.presentation.models.StayFilterType
import com.fstengineering.leasilycrm.core.presentation.theme.customColors
import com.fstengineering.leasilycrm.propertyStays.presentation.models.PropertyOccupiedPeriodsUiState
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyOccupiedDatesSection(
    occupiedPeriods: PropertyOccupiedPeriodsUiState.Loaded,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    onViewPeriod: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val occupiedDates = remember(occupiedPeriods.selected) {
        occupiedPeriods.selected.values.flatten()
    }

    val stayColorToTenantsNames = remember(occupiedPeriods.selected) {
        val idsGroup = occupiedDates.groupBy { it.periodId }

        idsGroup
            .filter { (_, dates) -> !dates.first().isUnavailablePeriod }
            .map { (periodId, dates) ->
                val firstDate = dates.first()

                val tenantColor = firstDate.secondHalfPeriodIndex
                    ?.let { i -> @Composable { customColors[i] } }
                    ?: @Composable { Color.Transparent }

                val tenantName = firstDate.tenantName
                    ?: context.getString(R.string.unknown_text)

                Triple(periodId, tenantColor, tenantName)
            }
            .toPersistentList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                        MonthYearFilterField(
//                            placeholder = stringResource(R.string.property_stays_start_month_year_filter_field_placeholder),
//                            monthYearValue = startMonthYearValue,
//                            onTap = { mustShowStartMonthYearFilterComboBottomsheet = true },
//                            modifier = Modifier
//                                .weight(1f)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = stringResource(R.string.until_label),
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.onSurface,
//                            modifier = Modifier
//                                .offset(y = (-4).dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        MonthYearFilterField(
//                            placeholder = stringResource(R.string.property_stays_end_month_year_filter_field_placeholder),
//                            monthYearValue = endMonthYearValue,
//                            onTap = { mustShowEndMonthYearFilterComboBottomsheet = true },
//                            modifier = Modifier
//                                .weight(1f)
//                        )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (occupiedDates.isEmpty()) {
                StaysEmptySection(stayFilterType = StayFilterType.All)
            } else {
                OccupiedDatesCalendarsSection(
                    occupiedPeriods = occupiedPeriods,
                    stayColorToTenantsNames = stayColorToTenantsNames,
                    hasUnavailablePeriod = occupiedDates.any { it.isUnavailablePeriod },
                    onViewPeriod = onViewPeriod,
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
