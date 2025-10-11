package com.fstengineering.leasilycrm.propertyStays.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.customColors
import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.StaysEmptySection
import com.fstengineering.leasilycrm.core.presentation.models.StayFilterType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyStaysSection(
    property: Property,
    selectedYearStaysDates: StaysStructuredDatesMap,
    selectedYear: Int,
    onYearSelect: (Int) -> Unit,
    onViewStay: (Long) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()

    val yearOptions = remember(property.stays) {
        val checkInYears = property.stays.mapNotNull { it.checkInDateTime?.year?.toString() }
        val checkOutYears = property.stays.mapNotNull { it.checkOutDateTime?.year?.toString() }

        checkInYears
            .toMutableList()
            .apply { addAll(checkOutYears) }
            .toSet()
            .toPersistentList()
    }

    val stayColorToTenantsNames = remember(selectedYearStaysDates) {
        if (selectedYearStaysDates.isEmpty()) return@remember persistentListOf()

        val idsGroup = selectedYearStaysDates.values
            .flatten()
            .groupBy { it.stayId }

        idsGroup
            .filter { (_, stayDates) ->
                val isUnavailablePeriod =
                    stayDates.first().secondHalfPeriodIndex == UNAVAILABLE_PERIOD_INDEX
                !isUnavailablePeriod
            }
            .map { (stayId, stayDates) ->
                val stayTenantColor = stayDates.first().secondHalfPeriodIndex
                    ?.let { i -> @Composable { customColors[i] } }
                    ?: @Composable { Color.Transparent }

                val tenantName = property.stays
                    .find { it.id == stayId }?.tenant?.name
                    ?: context.getString(R.string.unknown_text)

                Triple(stayId, stayTenantColor, tenantName)
            }
            .toPersistentList()
    }

    var mustShowYearFilterComboBottomsheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = isLoading,
        ) { isLoading ->
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppCircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                    )
                }
            } else {
                Column {
                    YearFilterField(
                        selectedYear = selectedYear,
                        onTap = { mustShowYearFilterComboBottomsheet = true },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (selectedYearStaysDates.isEmpty()) {
                        StaysEmptySection(stayFilterType = StayFilterType.All)
                    } else {
                        StaysCalendarList(
                            stayColorToTenantsNames = stayColorToTenantsNames,
                            stays = property.stays,
                            selectedYearStaysDates = selectedYearStaysDates,
                            onViewStay = onViewStay,
                        )
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }
        }
    }
    if (mustShowYearFilterComboBottomsheet) {
        YearFilterComboBottomsheet(
            sheetState = sheetState,
            yearOptions = yearOptions,
            onYearSelect = onYearSelect,
            onDismiss = { mustShowYearFilterComboBottomsheet = false },
        )
    }
}
