package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.presentation.composables.OccupiedPeriodsCalendar
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCalendarOccupiedDateUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun StayPeriodDetailsItem(
    occupiedDatesGrouped: ImmutableMap<AppYearMonth, ImmutableList<PropertyCalendarOccupiedDateUiState>>,
    isUnavailablePeriod: Boolean,
    modifier: Modifier = Modifier,
) {
    val defaultPeriodColor = MaterialTheme.colorScheme.secondaryContainer

    val periodDailiesCount = occupiedDatesGrouped.values.flatten().size - 1

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.stay_registration_period_label),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            occupiedDatesGrouped.entries.forEachIndexed { i, (monthYear, stayDates) ->
                if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                OccupiedPeriodsCalendar(
                    month = monthYear.month,
                    year = monthYear.year,
                    periodDailiesCount = if (isUnavailablePeriod) null else periodDailiesCount,
                    occupiedPeriods = monthYear to stayDates,
                    singlePeriodColor = defaultPeriodColor,
                )
            }
        }
    }
}
