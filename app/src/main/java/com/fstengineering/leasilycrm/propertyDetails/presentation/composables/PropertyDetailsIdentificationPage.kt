package com.fstengineering.leasilycrm.propertyDetails.presentation.composables

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
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyIncludedItemsDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyLocationDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyObservationsDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyPicturesDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertySeaProximityTypeDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items.PropertyTypeDetailsItem
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyUiState

@Composable
fun PropertyDetailsIdentificationPage(
    property: PropertyUiState.Loaded,
    onViewPropertyPicture: (PropertyFileUiState) -> Unit,
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
        PropertyLocationDetailsItem(propertyAddress = property.address)
        Spacer(modifier = Modifier.height(24.dp))
        PropertyQuantityCharacteristicsDetailsItems(qtCharacteristics = property.quantityCharacteristics)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        PropertySeaProximityTypeDetailsItem(
            seaProximityTypeId = property.seaProximityTypeId,
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
        PropertyOtherCharacteristicsDetailsItems(checkedCharacteristics = property.characteristics)
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
