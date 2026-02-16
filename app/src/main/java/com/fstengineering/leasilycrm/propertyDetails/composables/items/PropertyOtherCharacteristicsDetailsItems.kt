package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.presentation.composables.HasCharacteristicRow
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PropertyOtherCharacteristicsDetailsItems(
    property: Property,
    propertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    modifier: Modifier = Modifier,
) {
    val checkedCharacteristics = remember(property.characteristics) {
        property.characteristics
    }

    val notCheckedCharacteristics = remember(checkedCharacteristics) {
        propertyCharacteristics.filter {
            it.id !in checkedCharacteristics
                .map { characteristic -> characteristic.propertyCharacteristic?.id }
        }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.property_registration_other_characteristics_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        checkedCharacteristics.forEach { checkedCharacteristic ->
            Spacer(modifier = Modifier.height(16.dp))
            HasCharacteristicRow(
                hasCharacteristic = true,
                characteristicLabel = checkedCharacteristic.propertyCharacteristic?.name ?: "",
            )
        }
        notCheckedCharacteristics.forEach { notCheckedCharacteristic ->
            Spacer(modifier = Modifier.height(16.dp))
            HasCharacteristicRow(
                hasCharacteristic = false,
                characteristicLabel = notCheckedCharacteristic.name,
            )
        }
    }
}
