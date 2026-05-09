package com.fstengineering.leasilycrm.leadTenants.presentation.tenants.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.FilterConnectedButton
import com.fstengineering.leasilycrm.core.presentation.models.FilterConnectedButtonPosition
import com.fstengineering.leasilycrm.leadTenants.presentation.models.TenantFilterType

@Composable
fun TenantFilterButtonGroup(
    selectedFilterType: TenantFilterType,
    onFilterTypeSelect: (TenantFilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        FilterConnectedButton(
            label = stringResource(TenantFilterType.UpcomingStay.label),
            isSelected = selectedFilterType == TenantFilterType.UpcomingStay,
            position = FilterConnectedButtonPosition.Start,
            onFilterTypeSelect = { onFilterTypeSelect(TenantFilterType.UpcomingStay) },
            modifier = Modifier
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(TenantFilterType.Old.label),
            isSelected = selectedFilterType == TenantFilterType.Old,
            position = FilterConnectedButtonPosition.End,
            onFilterTypeSelect = { onFilterTypeSelect(TenantFilterType.Old) },
            modifier = Modifier
                .weight(1f)
        )
    }
}
