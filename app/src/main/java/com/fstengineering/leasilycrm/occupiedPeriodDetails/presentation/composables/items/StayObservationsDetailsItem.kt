package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun StayObservationsDetailsItem(
    observations: String?,
    isUnavailablePeriod: Boolean,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.AutoMirrored.Outlined.Notes,
        label = stringResource(
            when {
                isUnavailablePeriod -> R.string.stay_registration_unavailable_period_observations_label
                else -> R.string.stay_registration_observations_label
            },
        ),
        value = observations ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = observations == null,
        modifier = modifier
    )
}
