package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items.StayCheckInCheckOutDetailsItem
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items.StayObservationsDetailsItem
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items.StayPeriodDetailsItem
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items.StayPropertyDetailsItem
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.OccupiedPeriodDetailsUiState

@Composable
fun UnavailablePeriodDetailsSection(
    unavailablePeriod: OccupiedPeriodDetailsUiState.Loaded,
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
                property = unavailablePeriod.property,
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
                occupiedDatesGrouped = unavailablePeriod.occupiedDatesGrouped,
                isUnavailablePeriod = unavailablePeriod.isUnavailablePeriod,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayCheckInCheckOutDetailsItem(
                checkInDateTime = unavailablePeriod.checkInDateTime,
                checkOutDateTime = unavailablePeriod.checkOutDateTime,
                checkInHourMinute = unavailablePeriod.checkInHourMinute,
                checkOutHourMinute = unavailablePeriod.checkOutHourMinute,
                isAutoCheckIn = unavailablePeriod.isAutoCheckIn,
                isAutoCheckOut = unavailablePeriod.isAutoCheckOut,
                isCheckInDone = unavailablePeriod.isCheckInDone,
                isCheckOutDone = unavailablePeriod.isCheckOutDone,
                isUnavailablePeriod = unavailablePeriod.isUnavailablePeriod,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayObservationsDetailsItem(
                observations = unavailablePeriod.observations,
                isUnavailablePeriod = unavailablePeriod.isUnavailablePeriod,
            )
        }
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}
