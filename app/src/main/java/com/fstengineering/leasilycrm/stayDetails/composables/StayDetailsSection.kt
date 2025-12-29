package com.fstengineering.leasilycrm.stayDetails.composables

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
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayCheckInCheckOutDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayCommissionDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayObservationsDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayPeriodDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayPropertyDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayQtGuestsDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayTenantDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayTenantVehicleDataDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayTotalPriceDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.StayTransfersDetailsItem
import com.fstengineering.leasilycrm.stayDetails.composables.items.stayDocumentsDetailsItem

@Composable
fun StayDetailsSection(
    stay: Stay,
    stayStructuredDates: StaysStructuredDatesMap,
    onOpenStayFile: (StayFile) -> Unit,
    cameFromProperty: Boolean,
    cameFromTenant: Boolean,
    onViewPropertyDashboard: () -> Unit,
    onViewTenantDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasAnyVehicleData = stay.vehicleMake != null
            || stay.vehicleColor != null
            || stay.vehicleLicensePlate != null

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
            StayTenantDetailsItem(
                tenant = stay.tenant,
                cameFromTenant = cameFromTenant,
                onViewTenantDetails = onViewTenantDetails,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        if (hasAnyVehicleData) {
            item {
                StayTenantVehicleDataDetailsItem(
                    vehicleMake = stay.vehicleMake,
                    vehicleColor = stay.vehicleColor,
                    vehicleLicensePlate = stay.vehicleLicensePlate,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text(
                text = stringResource(R.string.stay_registration_stay_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { StayQtGuestsDetailsItem(qtGuests = stay.qtGuests) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { StayPeriodDetailsItem(stayStructuredDates = stayStructuredDates) }
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
        item { StayTotalPriceDetailsItem(totalPrice = stay.totalPrice) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { StayCommissionDetailsItem(commission = stay.commission) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { StayTransfersDetailsItem(transfers = stay.transfers) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        stayDocumentsDetailsItem(
            files = stay.files,
            onOpenStayFile = onOpenStayFile,
        )
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
