package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fstengineering.leasilycrm.R

@Composable
fun CheckInCheckOutChipLabel(
    isCheckIn: Boolean,
    isUnavailablePeriod: Boolean,
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
                        if (isUnavailablePeriod) R.string.stay_card_start_chip_label_text
                        else R.string.stay_card_check_in_chip_label_text
                    }

                    else -> {
                        if (isUnavailablePeriod) R.string.stay_card_end_chip_label_text
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
