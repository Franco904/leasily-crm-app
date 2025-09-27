package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantCityDetailsItem(
    city: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.LocationCity,
        label = stringResource(R.string.lead_tenant_registration_city_label),
        value = city ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = city == null,
        modifier = modifier
    )
}
