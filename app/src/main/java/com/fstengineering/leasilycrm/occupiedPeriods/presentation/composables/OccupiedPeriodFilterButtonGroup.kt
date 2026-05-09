package com.fstengineering.leasilycrm.occupiedPeriods.presentation.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.FilterConnectedButton
import com.fstengineering.leasilycrm.core.presentation.models.FilterConnectedButtonPosition
import com.fstengineering.leasilycrm.core.presentation.models.OccupiedPeriodFilterType
import com.fstengineering.leasilycrm.home.presentation.composables.NumberRedBadge
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate

@Composable
fun OccupiedPeriodFilterButtonGroup(
    selectedFilterType: OccupiedPeriodFilterType,
    onFilterTypeSelect: (OccupiedPeriodFilterType) -> Unit,
    buttonsCoords: PersistentMap<OccupiedPeriodFilterType, LayoutCoordinates?>,
    onButtonGroupCoords: (PersistentMap<OccupiedPeriodFilterType, LayoutCoordinates?>) -> Unit,
    staysAbleToCheckInCount: Int,
    staysAbleToCheckOutCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        FilterConnectedButton(
            label = stringResource(OccupiedPeriodFilterType.UpcomingCheckIn.label),
            isSelected = selectedFilterType == OccupiedPeriodFilterType.UpcomingCheckIn,
            position = FilterConnectedButtonPosition.Start,
            onFilterTypeSelect = { onFilterTypeSelect(OccupiedPeriodFilterType.UpcomingCheckIn) },
            suffix = if (staysAbleToCheckInCount == 0) null else {
                {
                    NumberRedBadge(number = staysAbleToCheckInCount)
                }
            },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onButtonGroupCoords(
                        buttonsCoords.mutate {
                            it[OccupiedPeriodFilterType.UpcomingCheckIn] = coords
                        },
                    )
                },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(OccupiedPeriodFilterType.UpcomingCheckOut.label),
            isSelected = selectedFilterType == OccupiedPeriodFilterType.UpcomingCheckOut,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(OccupiedPeriodFilterType.UpcomingCheckOut) },
            suffix = if (staysAbleToCheckOutCount == 0) null else {
                {
                    NumberRedBadge(number = staysAbleToCheckOutCount)
                }
            },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onButtonGroupCoords(
                        buttonsCoords.mutate {
                            it[OccupiedPeriodFilterType.UpcomingCheckOut] = coords
                        },
                    )
                },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(OccupiedPeriodFilterType.Finished.label),
            isSelected = selectedFilterType == OccupiedPeriodFilterType.Finished,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(OccupiedPeriodFilterType.Finished) },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onButtonGroupCoords(
                        buttonsCoords.mutate { it[OccupiedPeriodFilterType.Finished] = coords },
                    )
                },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(OccupiedPeriodFilterType.UnavailablePeriod.label),
            isSelected = selectedFilterType == OccupiedPeriodFilterType.UnavailablePeriod,
            position = FilterConnectedButtonPosition.Center,
            onFilterTypeSelect = { onFilterTypeSelect(OccupiedPeriodFilterType.UnavailablePeriod) },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onButtonGroupCoords(
                        buttonsCoords.mutate {
                            it[OccupiedPeriodFilterType.UnavailablePeriod] = coords
                        },
                    )
                },
        )
        Spacer(modifier = Modifier.width(2.dp))
        FilterConnectedButton(
            label = stringResource(OccupiedPeriodFilterType.All.label),
            isSelected = selectedFilterType == OccupiedPeriodFilterType.All,
            position = FilterConnectedButtonPosition.End,
            onFilterTypeSelect = { onFilterTypeSelect(OccupiedPeriodFilterType.All) },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onButtonGroupCoords(
                        buttonsCoords.mutate { it[OccupiedPeriodFilterType.All] = coords },
                    )
                },
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}
