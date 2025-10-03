package com.fstengineering.leasilycrm.home.stays.composables

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
import com.fstengineering.leasilycrm.home.stays.models.StayFilterType

@Composable
fun StayFilterButtonGroup(
    selectedFilterType: StayFilterType,
    onFilterTypeSelect: (StayFilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        FilterConnectedButton(
            label = stringResource(StayFilterType.UpcomingCheckIn.label),
            isSelected = selectedFilterType == StayFilterType.UpcomingCheckIn,
            position = FilterConnectedButtonPosition.Start,
            onFilterTypeSelect = { onFilterTypeSelect(StayFilterType.UpcomingCheckIn) },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(StayFilterType.UpcomingCheckOut.label),
            isSelected = selectedFilterType == StayFilterType.UpcomingCheckOut,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(StayFilterType.UpcomingCheckOut) },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(StayFilterType.Finished.label),
            isSelected = selectedFilterType == StayFilterType.Finished,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(StayFilterType.Finished) },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(StayFilterType.UnavailablePeriod.label),
            isSelected = selectedFilterType == StayFilterType.UnavailablePeriod,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(StayFilterType.UnavailablePeriod) },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(StayFilterType.All.label),
            isSelected = selectedFilterType == StayFilterType.All,
            position = FilterConnectedButtonPosition.End,
            onFilterTypeSelect = { onFilterTypeSelect(StayFilterType.All) },
        )
    }
}
