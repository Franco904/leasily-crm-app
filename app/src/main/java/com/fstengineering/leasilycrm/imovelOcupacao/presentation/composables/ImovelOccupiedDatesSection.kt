package com.fstengineering.leasilycrm.imovelOcupacao.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.OccupiedPeriodsEmptySection
import com.fstengineering.leasilycrm.core.presentation.models.OccupiedPeriodFilterType
import com.fstengineering.leasilycrm.core.presentation.theme.customColors
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelPeriodosOcupadosUiState
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelOccupiedDatesSection(
    occupiedPeriods: ImovelPeriodosOcupadosUiState.Loaded,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    onViewPeriod: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val occupiedDates = remember(occupiedPeriods.selected) {
        occupiedPeriods.selected.values.flatten()
    }

    val stayColorToTenantsNames = remember(occupiedPeriods.selected) {
        val idsGroup = occupiedDates.groupBy { it.periodId }

        idsGroup
            .filter { (_, dates) -> !dates.first().isUnavailablePeriod }
            .map { (periodId, dates) ->
                val firstDate = dates.first()

                val tenantColor = firstDate.secondHalfPeriodIndex
                    ?.let { i -> @Composable { customColors[i] } }
                    ?: @Composable { Color.Transparent }

                val tenantName = firstDate.tenantName
                    ?: context.getString(R.string.unknown_text)

                Triple(periodId, tenantColor, tenantName)
            }
            .toPersistentList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            if (occupiedDates.isEmpty()) {
                OccupiedPeriodsEmptySection(occupiedPeriodFilterType = OccupiedPeriodFilterType.All)
            } else {
                OccupiedDatesCalendarsSection(
                    occupiedPeriods = occupiedPeriods,
                    stayColorToTenantsNames = stayColorToTenantsNames,
                    hasUnavailablePeriod = occupiedDates.any { it.isUnavailablePeriod },
                    onViewPeriod = onViewPeriod,
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
