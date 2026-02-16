package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomSheet
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelAbbreviationByNumber
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartMonthYearFilterComboBottomsheet(
    sheetState: SheetState,
    monthYearOptions: ImmutableList<CalendarMonthYear>,
    onMonthYearSelect: (CalendarMonthYear) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val monthYearOptionsMap = remember(monthYearOptions) {
        monthYearOptions
            .associateBy { monthYear ->
                val abbr = context
                    .getMonthLabelAbbreviationByNumber(monthNumber = monthYear.month)
                    .lowercase()
                    .capitalizeFirst()
                "$abbr ${monthYear.year}"
            }
            .toPersistentMap()
    }

    AppComboBottomSheet(
        sheetState = sheetState,
        options = monthYearOptionsMap.keys.toPersistentList(),
        onOptionSelect = { monthYearOption ->
            val option = monthYearOptionsMap[monthYearOption] ?: return@AppComboBottomSheet
            onMonthYearSelect(option)
        },
        onDismissRequest = onDismiss,
        emptyOptionsIcon = Icons.Outlined.Grain,
        emptyOptionsText = stringResource(R.string.property_stays_month_year_filter_field_empty_text),
        modifier = modifier
    )
}
