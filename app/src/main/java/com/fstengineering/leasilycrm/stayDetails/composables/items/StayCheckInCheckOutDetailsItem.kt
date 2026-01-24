package com.fstengineering.leasilycrm.stayDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.presentation.composables.CheckInCheckOutChipLabel
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import com.fstengineering.leasilycrm.core.presentation.utils.rememberNow

@Composable
fun StayCheckInCheckOutDetailsItem(
    checkInDateTime: AppDateTime?,
    checkOutDateTime: AppDateTime?,
    checkInHourMinute: Pair<Int?, Int?>,
    checkOutHourMinute: Pair<Int?, Int?>,
    isAutoCheckIn: Boolean,
    isAutoCheckOut: Boolean,
    isCheckInDone: Boolean,
    isCheckOutDone: Boolean,
    isUnavailablePeriod: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val now by rememberNow()

    val checkInDateTimeStartOfDay = checkInDateTime?.toStartOfDay()
    val checkOutDateTimeStartOfDay = checkOutDateTime?.toStartOfDay()

    var checkInChipWidthDp by remember { mutableStateOf(Dp.Unspecified) }
    var checkOutChipWidthDp by remember { mutableStateOf(Dp.Unspecified) }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.stay_details_check_in_check_out_times_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckInCheckOutChipLabel(
                isCheckIn = true,
                isUnavailablePeriod = isUnavailablePeriod,
                isActive = when {
                    isUnavailablePeriod -> now.isBefore(checkInDateTimeStartOfDay)
                    else -> !isCheckInDone
                },
                modifier = Modifier
                    .width(if (isUnavailablePeriod) checkInChipWidthDp else checkOutChipWidthDp)
                    .onSizeChanged { size ->
                        checkInChipWidthDp = with(density) { size.width.toDp() }
                    }
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (checkInHourMinute.toList().all { it != null }) {
                Text(
                    text = stringResource(
                        R.string.stay_details_check_in_check_out_time,
                        checkInHourMinute.first!!.formatToTwoDigitsDateTime(),
                        checkInHourMinute.second!!.formatToTwoDigitsDateTime(),
                        if (isAutoCheckIn) stringResource(R.string.stay_auto_check_in_suffix_text) else "",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckInCheckOutChipLabel(
                isCheckIn = false,
                isUnavailablePeriod = isUnavailablePeriod,
                isActive = when {
                    isUnavailablePeriod -> {
                        now.isAfter(checkInDateTimeStartOfDay)
                                && now.isBefore(checkOutDateTimeStartOfDay)
                    }

                    else -> isCheckInDone && !isCheckOutDone
                },
                modifier = Modifier
                    .width(if (isUnavailablePeriod) checkInChipWidthDp else checkOutChipWidthDp)
                    .onSizeChanged { size ->
                        checkOutChipWidthDp = with(density) { size.width.toDp() }
                    }
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (checkOutHourMinute.toList().all { it != null }) {
                Text(
                    text = stringResource(
                        R.string.stay_details_check_in_check_out_time,
                        checkOutHourMinute.first!!.formatToTwoDigitsDateTime(),
                        checkOutHourMinute.second!!.formatToTwoDigitsDateTime(),
                        if (isAutoCheckOut) stringResource(R.string.stay_auto_check_out_suffix_text) else "",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}
