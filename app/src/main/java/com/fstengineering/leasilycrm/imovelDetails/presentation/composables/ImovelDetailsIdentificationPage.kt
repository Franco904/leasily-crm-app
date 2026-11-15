package com.fstengineering.leasilycrm.imovelDetails.presentation.composables

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
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelIncludedItemsDetailsItem
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelLocationDetailsItem
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelObservationsDetailsItem
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelPicturesDetailsItems
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelSeaProximityTypeDetailsItem
import com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items.ImovelTypeDetailsItem
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState

@Composable
fun ImovelDetailsIdentificationPage(
    imovel: ImovelUiState.Loaded,
    onViewImovelPicture: (ImovelFileUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        ImovelTypeDetailsItem(imovelType = imovel.type)
        Spacer(modifier = Modifier.height(24.dp))
        ImovelLocationDetailsItem(imovelAddress = imovel.address)
        Spacer(modifier = Modifier.height(24.dp))
        ImovelQuantityCharacteristicsDetailsItems(qtCharacteristics = imovel.quantityCharacteristics)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        ImovelSeaProximityTypeDetailsItem(
            seaProximityTypeId = imovel.seaProximityTypeId,
            distanceToSea = imovel.distanceToSea,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ImovelIncludedItemsDetailsItem(
            includedItems = imovel.includedItems,
            hasIncludedItems = imovel.hasIncludedItems == true,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        ImovelOtherCharacteristicsDetailsItems(checkedCharacteristics = imovel.characteristics)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        ImovelPicturesDetailsItems(
            pictures = imovel.files,
            onViewImovelPicture = onViewImovelPicture,
        )
        Spacer(modifier = Modifier.height(24.dp))
        ImovelObservationsDetailsItem(observations = imovel.observations)
        Spacer(modifier = Modifier.height(120.dp))
    }
}
