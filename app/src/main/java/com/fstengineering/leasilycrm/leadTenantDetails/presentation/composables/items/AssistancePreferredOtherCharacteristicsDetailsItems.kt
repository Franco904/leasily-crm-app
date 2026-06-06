package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.HasCharacteristicRow
import com.fstengineering.leasilycrm.core.presentation.utils.presentationLabel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AssistancePreferredOtherCharacteristicsDetailsItems(
    checkedCharacteristics: ImmutableList<PropertyCharacteristic>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.property_registration_other_characteristics_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        checkedCharacteristics.forEach { characteristic ->
            Spacer(modifier = Modifier.height(16.dp))
            HasCharacteristicRow(
                hasCharacteristic = true,
                characteristicLabel = stringResource(characteristic.presentationLabel()),
            )
        }
    }
}
