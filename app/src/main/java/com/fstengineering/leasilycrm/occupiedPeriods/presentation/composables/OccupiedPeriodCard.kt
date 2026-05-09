package com.fstengineering.leasilycrm.occupiedPeriods.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.composables.CheckInCheckOutChipLabel
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import com.fstengineering.leasilycrm.core.presentation.utils.rememberNow
import com.fstengineering.leasilycrm.occupiedPeriods.application.models.dtos.OccupiedPeriodListReadModel
import kotlinx.collections.immutable.toPersistentList

@Composable
fun OccupiedPeriodCard(
    occupiedPeriod: OccupiedPeriodListReadModel,
    onCardClick: () -> Unit,
    onEditPeriod: () -> Unit,
    onDeletePeriod: () -> Unit,
    onRegisterStayCheckIn: () -> Unit,
    onRegisterStayCheckOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val now by rememberNow()

    val checkInDateTimeStartOfDay = occupiedPeriod.checkInDateTime.toStartOfDay()
    val checkOutDateTimeStartOfDay = occupiedPeriod.checkOutDateTime.toStartOfDay()

    val canRegisterCheckIn = !occupiedPeriod.isCheckInDone
            && now.isAfter(checkInDateTimeStartOfDay)
            && !occupiedPeriod.isUnavailablePeriod

    val canRegisterCheckOut = !occupiedPeriod.isCheckOutDone
            && now.isAfter(checkOutDateTimeStartOfDay)
            && !occupiedPeriod.isUnavailablePeriod

    var checkInChipWidthDp by remember(occupiedPeriod.isUnavailablePeriod) {
        mutableStateOf(Dp.Unspecified)
    }
    var checkOutChipWidthDp by remember(occupiedPeriod.isUnavailablePeriod) {
        mutableStateOf(Dp.Unspecified)
    }

    AppCard(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (occupiedPeriod.isUnavailablePeriod) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Block,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.stay_type_unavailable_period_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        Text(
                            text = occupiedPeriod.tenantName ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (canRegisterCheckIn || canRegisterCheckOut) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                        )
                    }
                }
                AppMoreOptionsIconDropdownMenu(
                    options = buildList {
                        add(
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.EditCalendar,
                                text = stringResource(R.string.edit_action_button_text),
                                onClick = onEditPeriod,
                            )
                        )
                        if (!occupiedPeriod.isCheckOutDone) {
                            add(
                                MoreOptionsMenuOption(
                                    iconDrawable = R.drawable.event_busy,
                                    text = stringResource(R.string.delete_action_button_text),
                                    onClick = onDeletePeriod,
                                )
                            )
                        }
                    }.toPersistentList(),
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(
                    id = R.string.stay_card_subtitle_text,
                    occupiedPeriod.propertyType?.name ?: "",
                    occupiedPeriod.propertyNeighborhood ?: "",
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckInCheckOutChipLabel(
                    isCheckIn = true,
                    isUnavailablePeriod = occupiedPeriod.isUnavailablePeriod,
                    isActive = when {
                        occupiedPeriod.isUnavailablePeriod -> now.isBefore(checkInDateTimeStartOfDay)
                        else -> !occupiedPeriod.isCheckInDone
                    },
                    modifier = Modifier
                        .width(if (occupiedPeriod.isUnavailablePeriod) checkInChipWidthDp else checkOutChipWidthDp)
                        .onSizeChanged { size ->
                            checkInChipWidthDp = with(density) { size.width.toDp() }
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.stay_check_in_check_out_date_time_text,
                        occupiedPeriod.checkInDateTime.dayOfMonth.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkInDateTime.month.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkInDateTime.year.toString(),
                        occupiedPeriod.checkInHourMinute.first.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkInHourMinute.second.formatToTwoDigitsDateTime(),
                        if (occupiedPeriod.isAutoCheckIn) stringResource(R.string.stay_auto_check_in_suffix_text) else "",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckInCheckOutChipLabel(
                    isCheckIn = false,
                    isUnavailablePeriod = occupiedPeriod.isUnavailablePeriod,
                    isActive = when {
                        occupiedPeriod.isUnavailablePeriod -> {
                            now.isAfter(checkInDateTimeStartOfDay)
                                    && now.isBefore(checkOutDateTimeStartOfDay)
                        }

                        else -> occupiedPeriod.isCheckInDone && !occupiedPeriod.isCheckOutDone
                    },
                    modifier = Modifier
                        .width(if (occupiedPeriod.isUnavailablePeriod) checkInChipWidthDp else checkOutChipWidthDp)
                        .onSizeChanged { size ->
                            checkOutChipWidthDp = with(density) { size.width.toDp() }
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.stay_check_in_check_out_date_time_text,
                        occupiedPeriod.checkOutDateTime.dayOfMonth.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkOutDateTime.month.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkOutDateTime.year.toString(),
                        occupiedPeriod.checkOutHourMinute.first.formatToTwoDigitsDateTime(),
                        occupiedPeriod.checkOutHourMinute.second.formatToTwoDigitsDateTime(),
                        if (occupiedPeriod.isAutoCheckOut) stringResource(R.string.stay_auto_check_out_suffix_text) else "",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
            if (canRegisterCheckIn || canRegisterCheckOut) {
                val isCheckIn = canRegisterCheckIn && !canRegisterCheckOut

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                RegisterCheckInCheckOutStayCardButton(
                    isCheckIn = isCheckIn,
                    onRegister = if (isCheckIn) onRegisterStayCheckIn else onRegisterStayCheckOut,
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun RegisterCheckInCheckOutStayCardButton(
    isCheckIn: Boolean,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        onClick = onRegister,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(
                if (isCheckIn) R.string.register_check_in_button_text
                else R.string.register_check_out_button_text
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
