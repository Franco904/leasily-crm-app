package com.fstengineering.leasilycrm.home.stays.composables

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun StayCard(
    stay: Stay,
    onCardClick: () -> Unit,
    onEditStay: () -> Unit,
    onDeleteStay: () -> Unit,
    onRegisterCheckIn: () -> Unit,
    onRegisterCheckOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val now by rememberNow()

    val checkInDateTimeStartOfDay = stay.checkInDateTime
        ?.withHour(0)
        ?.withMinute(0)

    val checkOutDateTimeStartOfDay = stay.checkOutDateTime
        ?.withHour(0)
        ?.withMinute(0)

    val canRegisterCheckIn =
        !stay.isCheckInDone && now > checkInDateTimeStartOfDay && !stay.isFromOtherRealtor
    val canRegisterCheckOut =
        !stay.isCheckOutDone && now > checkOutDateTimeStartOfDay && !stay.isFromOtherRealtor

    var checkInChipWidthDp by remember { mutableStateOf(Dp.Unspecified) }
    var checkOutChipWidthDp by remember { mutableStateOf(Dp.Unspecified) }

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
                    if (stay.isFromOtherRealtor) {
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
                                text = stringResource(R.string.stay_type_other_realtor_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        Text(
                            text = stay.tenant?.name ?: "",
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
                AppMoreOptionsMenu(
                    options = persistentListOf(
                        MoreOptionsMenuOption(
                            icon = Icons.Outlined.Edit,
                            text = stringResource(R.string.edit_action_button_text),
                            onClick = onEditStay,
                        ),
                        MoreOptionsMenuOption(
                            icon = Icons.Outlined.Delete,
                            text = stringResource(R.string.delete_action_button_text),
                            onClick = onDeleteStay,
                        ),
                    ),
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(
                    id = R.string.stay_card_subtitle_text,
                    stay.property?.type?.name ?: "",
                    stay.property?.neighborhood ?: "",
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
                    isStayFromOtherRealtor = stay.isFromOtherRealtor,
                    isActive = !stay.isCheckInDone,
                    modifier = Modifier
                        .width(if (stay.isFromOtherRealtor) checkInChipWidthDp else checkOutChipWidthDp)
                        .onSizeChanged { size ->
                            checkInChipWidthDp = with(density) { size.width.toDp() }
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.stay_check_in_date_text,
                        stay.checkInDateTime!!.dayOfMonth.formatToTwoDigitsDateTime(),
                        stay.checkInDateTime.monthValue.formatToTwoDigitsDateTime(),
                        stay.checkInDateTime.year.toString(),
                        stay.checkInHourMinute.first!!.formatToTwoDigitsDateTime(),
                        stay.checkInHourMinute.second!!.formatToTwoDigitsDateTime(),
                        if (stay.isAutoCheckIn) stringResource(R.string.stay_auto_check_in_suffix_text) else "",
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
                    isStayFromOtherRealtor = stay.isFromOtherRealtor,
                    isActive = stay.isCheckInDone && !stay.isCheckOutDone,
                    modifier = Modifier
                        .width(if (stay.isFromOtherRealtor) checkInChipWidthDp else checkOutChipWidthDp)
                        .onSizeChanged { size ->
                            checkOutChipWidthDp = with(density) { size.width.toDp() }
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.stay_check_in_date_text,
                        stay.checkOutDateTime!!.dayOfMonth.formatToTwoDigitsDateTime(),
                        stay.checkOutDateTime.monthValue.formatToTwoDigitsDateTime(),
                        stay.checkOutDateTime.year.toString(),
                        stay.checkOutHourMinute.first!!.formatToTwoDigitsDateTime(),
                        stay.checkOutHourMinute.second!!.formatToTwoDigitsDateTime(),
                        if (stay.isAutoCheckOut) stringResource(R.string.stay_auto_check_out_suffix_text) else "",
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
                RegisterCheckInCheckOutButton(
                    isCheckIn = isCheckIn,
                    onRegister = if (isCheckIn) onRegisterCheckIn else onRegisterCheckOut,
                    modifier = Modifier
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun CheckInCheckOutChipLabel(
    isCheckIn: Boolean,
    isStayFromOtherRealtor: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isActive -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            },
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(
                when {
                    isCheckIn -> {
                        if (isStayFromOtherRealtor) R.string.stay_card_start_chip_label_text
                        else R.string.stay_card_check_in_chip_label_text
                    }

                    else -> {
                        if (isStayFromOtherRealtor) R.string.stay_card_end_chip_label_text
                        else R.string.stay_card_check_out_chip_label_text
                    }
                }
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            ),
            color = when {
                isActive -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun RegisterCheckInCheckOutButton(
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

@Composable
fun rememberNow(interval: Duration = 1.minutes): State<LocalDateTime> {
    val now = remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = LocalDateTime.now()
            delay(interval)
        }
    }

    return now
}
