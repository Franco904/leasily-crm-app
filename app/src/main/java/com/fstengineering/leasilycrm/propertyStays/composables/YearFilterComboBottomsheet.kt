package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomsheet
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilterComboBottomsheet(
    sheetState: SheetState,
    yearOptions: ImmutableList<String>,
    onYearSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppComboBottomsheet(
        sheetState = sheetState,
        options = yearOptions,
        onOptionSelect = { year -> onYearSelect(year.toInt()) },
        onDismissRequest = onDismiss,
        emptyOptionsIcon = Icons.Outlined.Grain,
        emptyOptionsText = stringResource(R.string.property_stays_year_filter_field_empty_text),
        modifier = modifier
    )
}
