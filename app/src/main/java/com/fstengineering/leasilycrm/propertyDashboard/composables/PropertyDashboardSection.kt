package com.fstengineering.leasilycrm.propertyDashboard.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.helpers.StayCalendarDate
import com.fstengineering.leasilycrm.core.presentation.composables.ColoredLabel
import com.fstengineering.leasilycrm.core.presentation.composables.StayPeriodsCalendar
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.theme.customColors
import com.fstengineering.leasilycrm.propertyOwners.presentation.composables.PropertyOwnerCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.roundToInt

@Composable
fun PropertyDashboardSection(
    property: Property,
    currentMonthStaysDates: Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>>,
    onViewPropertyDetails: () -> Unit,
    onViewPropertyOwnerDetails: () -> Unit,
    onViewAllPropertyStays: () -> Unit,
    onNewStay: () -> Unit,
    onViewStay: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val (currentMonthYear, staysDates) = currentMonthStaysDates

    val stayColorToTenantsNames = remember(currentMonthStaysDates) {
        val stayDates = currentMonthStaysDates.second
        if (stayDates.isEmpty()) return@remember persistentListOf()

        stayDates
            .groupBy { it.stayId }
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

    val stayColorToTenantsNamesChunked =
        when {
            stayColorToTenantsNames.isEmpty() -> {
                persistentListOf(persistentListOf(), persistentListOf())
            }

            stayColorToTenantsNames.size == 1 -> {
                val first = stayColorToTenantsNames.first()
                persistentListOf(persistentListOf(first), persistentListOf())
            }

            else -> {
                stayColorToTenantsNames
                    .chunked((stayColorToTenantsNames.size.toDouble() / 2).roundToInt())
                    .toImmutableList()
            }
        }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        PropertyCard(
            property = property,
            onViewPropertyDetails = onViewPropertyDetails,
            hasMoreOptionsMenu = false,
            hasCharacteristicsChipsRow = false,
            hasViewDetailsButton = true,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.property_dashboard_property_owner_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PropertyOwnerCard(
            propertyOwner = property.owner ?: return@Column,
            onViewPropertyOwnerDetails = onViewPropertyOwnerDetails,
            hasMoreOptionsMenu = false,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.property_dashboard_current_month_stays_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        StayPeriodsCalendar(
            month = currentMonthYear.month,
            year = currentMonthYear.year,
            selectedDatesWithMonthYear = if (staysDates.isEmpty()) null else currentMonthStaysDates,
            onViewStay = onViewStay,
            actions = {
                if (staysDates.isNotEmpty()) {
                    ViewAllPropertyStaysTextButton(onViewAllPropertyStays = onViewAllPropertyStays)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                NewStayPrimaryButton(onNewStay = onNewStay)
            },
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (stayColorToTenantsNamesChunked[1].isNotEmpty()) Modifier.widthIn(max = 360.dp)
                            else Modifier
                        )
                ) {
                    stayColorToTenantsNamesChunked[0]
                        .forEach { (stayId, stayTenantColor, tenantName) ->
                            ColoredLabel(
                                color = stayTenantColor(),
                                label = tenantName,
                                onClick = { onViewStay(stayId) },
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                }
                if (stayColorToTenantsNamesChunked[1].isNotEmpty()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 360.dp)
                    ) {
                        stayColorToTenantsNamesChunked[1]
                            .forEach { (stayId, stayTenantColor, tenantName) ->
                                ColoredLabel(
                                    color = stayTenantColor(),
                                    label = tenantName,
                                    onClick = { onViewStay(stayId) },
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                    }
                }
            }
            if (property.stays.any { it.isUnavailablePeriod }) {
                ColoredLabel(
                    color = null,
                    label = stringResource(R.string.stay_type_unavailable_period_title),
                )
            }
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}
