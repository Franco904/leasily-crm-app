package com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun PropertyObservationsDetailsItem(
    observations: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.AutoMirrored.Outlined.Notes,
        label = stringResource(R.string.property_registration_observations_label),
        value = observations ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = observations == null,
        modifier = modifier
    )
}
