package com.fstengineering.leasilycrm.clientes.presentation.hospedes.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.clientes.presentation.models.HospedeFilterType
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.FilterConnectedButton
import com.fstengineering.leasilycrm.core.presentation.models.FilterConnectedButtonPosition

@Composable
fun HospedeFilterButtonGroup(
    selectedFilterType: HospedeFilterType,
    onFilterTypeSelect: (HospedeFilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        FilterConnectedButton(
            label = stringResource(HospedeFilterType.ComEstadia.label),
            isSelected = selectedFilterType == HospedeFilterType.ComEstadia,
            position = FilterConnectedButtonPosition.Start,
            onFilterTypeSelect = { onFilterTypeSelect(HospedeFilterType.ComEstadia) },
            modifier = Modifier
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(HospedeFilterType.Antigo.label),
            isSelected = selectedFilterType == HospedeFilterType.Antigo,
            position = FilterConnectedButtonPosition.End,
            onFilterTypeSelect = { onFilterTypeSelect(HospedeFilterType.Antigo) },
            modifier = Modifier
                .weight(1f)
        )
    }
}
