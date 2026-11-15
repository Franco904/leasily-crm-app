package com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.House
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.utils.presentationLabel

@Composable
fun ImovelTypeDetailsItem(
    imovelType: ImovelType?,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = when {
            imovelType?.id == APARTMENT_PROPERTY_TYPE_ID -> Icons.Outlined.Apartment
            else -> Icons.Outlined.House
        },
        value = imovelType?.let { stringResource(it.presentationLabel()) } ?: "",
        modifier = modifier
    )
}
