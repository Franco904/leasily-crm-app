package com.fstengineering.leasilycrm.propertyDetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyIncludedItemsDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyLocationDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyObservationsDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyPicturesDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertySeaProximityTypeDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.composables.items.PropertyTypeDetailsItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PropertyDetailsIdentificationPage(
    property: Property,
    propertyCharacteristics: ImmutableList<PropertyCharacteristic>,
    onViewPropertyPicture: (PropertyFile) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        PropertyTypeDetailsItem(propertyType = property.type)
        Spacer(modifier = Modifier.height(24.dp))
        PropertyLocationDetailsItem(property = property)
        Spacer(modifier = Modifier.height(24.dp))
        PropertyQuantityCharacteristicsDetailsItems(property = property)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        PropertySeaProximityTypeDetailsItem(
            seaProximityType = property.seaProximityType,
            distanceToSea = property.distanceToSea,
        )
        Spacer(modifier = Modifier.height(24.dp))
        PropertyIncludedItemsDetailsItem(
            includedItems = property.includedItems,
            hasIncludedItems = property.hasIncludedItems == true,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        PropertyOtherCharacteristicsDetailsItems(
            property = property,
            propertyCharacteristics = propertyCharacteristics,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        PropertyPicturesDetailsItems(
            pictures = property.files,
            onViewPropertyPicture = onViewPropertyPicture,
        )
        Spacer(modifier = Modifier.height(24.dp))
        PropertyObservationsDetailsItem(observations = property.observations)
        Spacer(modifier = Modifier.height(120.dp))
    }
}
