package com.fstengineering.leasilycrm.stayDetails.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayCheckInCheckOutDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayObservationsDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayPeriodDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayPropertyDetailsItem

@Composable
fun UnavailablePeriodDetailsSection(
    stay: Stay,
    stayPeriodDateCircleColor: Color?,
    cameFromProperty: Boolean,
    onViewPropertyDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            StayPropertyDetailsItem(
                property = stay.property,
                cameFromProperty = cameFromProperty,
                onViewPropertyDashboard = onViewPropertyDashboard,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text(
                text = stringResource(R.string.stay_registration_period_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayPeriodDetailsItem(
                checkInDateTime = stay.checkInDateTime,
                checkOutDateTime = stay.checkOutDateTime,
                isUnavailablePeriod = stay.isUnavailablePeriod,
                dateCircleColor = stayPeriodDateCircleColor,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayCheckInCheckOutDetailsItem(
                checkInDateTime = stay.checkInDateTime,
                checkOutDateTime = stay.checkOutDateTime,
                checkInHourMinute = stay.checkInHourMinute,
                checkOutHourMinute = stay.checkOutHourMinute,
                isAutoCheckIn = stay.isAutoCheckIn,
                isAutoCheckOut = stay.isAutoCheckOut,
                isCheckInDone = stay.isCheckInDone,
                isCheckOutDone = stay.isCheckOutDone,
                isUnavailablePeriod = stay.isUnavailablePeriod,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayObservationsDetailsItem(
                observations = stay.observations,
                isUnavailablePeriod = stay.isUnavailablePeriod,
            )
        }
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}
