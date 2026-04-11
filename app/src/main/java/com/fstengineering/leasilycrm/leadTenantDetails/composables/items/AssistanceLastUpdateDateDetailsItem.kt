package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime

@Composable
fun AssistanceLastUpdateDateDetailsItem(
    updateDateTime: AppDateTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(
            id = R.string.lead_tenant_assistance_details_last_update_at_label,
            updateDateTime.dayOfMonth.formatToTwoDigitsDateTime(),
            updateDateTime.month.formatToTwoDigitsDateTime(),
            updateDateTime.year,
            updateDateTime.hour.formatToTwoDigitsDateTime(),
            updateDateTime.minute.formatToTwoDigitsDateTime(),
        ),
        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}
