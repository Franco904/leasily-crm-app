package com.fstengineering.leasilycrm.propertyDashboard.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.AppCalendar
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.models.CalendarMonthYear
import com.fstengineering.leasilycrm.propertyOwners.composables.PropertyOwnerCard
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.util.Calendar

@Composable
fun PropertyDashboardSection(
    property: Property,
    onViewPropertyDetails: () -> Unit,
    onViewPropertyOwnerDetails: () -> Unit,
    onViewNextStays: () -> Unit,
    onNewStay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val currentMonthYear = remember {
        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        CalendarMonthYear(month + year, month, year)
    }

    val currentMonthDates = remember(property) {
        property.stays
            .filter { it.checkInDateTime?.monthValue == currentMonthYear.month }
            .toPersistentList()
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
        AppCalendar(
            month = currentMonthYear.month,
            year = currentMonthYear.year,
            selectedDatesWithMonthYear = if (currentMonthDates.isEmpty()) null else {
                currentMonthYear to persistentListOf() // TODO: Map selected dates from stays datetimes
            },
            actions = {
                if (currentMonthDates.isEmpty()) {
                    TextButton(onClick = onViewNextStays) {
                        Text(
                            text = stringResource(R.string.view_all_a_action_button_text),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = onNewStay,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_add_on),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.new_property_stay_action_button_text),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
