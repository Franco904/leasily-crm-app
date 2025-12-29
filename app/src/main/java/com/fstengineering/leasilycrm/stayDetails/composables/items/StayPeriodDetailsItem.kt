package com.fstengineering.leasilycrm.stayDetails.composables.items

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
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.presentation.composables.StayPeriodsCalendar

@Composable
fun StayPeriodDetailsItem(
    stayStructuredDates: StaysStructuredDatesMap,
    modifier: Modifier = Modifier,
) {
    val defaultPeriodColor = MaterialTheme.colorScheme.secondaryContainer

    val isUnavailablePeriod = stayStructuredDates.values.flatten()
        .any { it.firstHalfPeriodIndex == -1 && it.secondHalfPeriodIndex == -1 }
    val periodDailiesCount = stayStructuredDates.values.flatten().size - 1

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
            stayStructuredDates.entries.forEachIndexed { i, (monthYear, stayDates) ->
                if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                StayPeriodsCalendar(
                    month = monthYear.month,
                    year = monthYear.year,
                    periodDailiesCount = if (isUnavailablePeriod) null else periodDailiesCount,
                    selectedDatesWithMonthYear = monthYear to stayDates,
                    singleStayPeriodColor = defaultPeriodColor,
                )
            }
        }
    }
}
