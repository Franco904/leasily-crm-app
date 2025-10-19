package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.details.BathroomsDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.BedroomsEnSuitesDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.DoubleSingleBedsDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.ParkingSpotsDetailsItem
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun AssistanceQuantityCharacteristicsDetailsItems(
    assistance: Assistance,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.lead_tenant_details_preferences_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchedDateRangeDetailsItem(
            searchedStartDate = assistance.searchedStartDate,
            searchedEndDate = assistance.searchedEndDate,
            hasFlexibilityForPeriod = assistance.hasFlexibilityForPeriod,
        )
        Spacer(modifier = Modifier.height(16.dp))
        MaxAcceptedNightlyRateDetailsItem(assistance.maxAcceptedNightlyRate)
        Spacer(modifier = Modifier.height(16.dp))
        GuestsDetailsItem(
            qtAdultGuests = assistance.qtAdultGuests,
            qtKidGuests = assistance.qtKidGuests,
            qtBabyGuests = assistance.qtBabyGuests,
            qtAnimalGuests = assistance.qtAnimalGuests,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BedroomsEnSuitesDetailsItem(
            qtBedrooms = assistance.qtBedrooms,
            qtEnSuites = assistance.qtEnSuites,
        )
        Spacer(modifier = Modifier.height(16.dp))
        DoubleSingleBedsDetailsItem(
            qtDoubleBeds = assistance.qtDoubleBeds,
            qtSingleBeds = assistance.qtSingleBeds,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BathroomsDetailsItem(qtBathrooms = assistance.qtBathrooms)
        Spacer(modifier = Modifier.height(16.dp))
        ParkingSpotsDetailsItem(qtParkingSpots = assistance.qtParkingSpots)
    }
}

@Composable
fun SearchedDateRangeDetailsItem(
    searchedStartDate: LocalDate?,
    searchedEndDate: LocalDate?,
    hasFlexibilityForPeriod: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val hasSearchedDateRange = searchedStartDate != null && searchedEndDate != null
    val dateRangeDaysCount = if (!hasSearchedDateRange) 0 else {
        ChronoUnit.DAYS
            .between(searchedStartDate, searchedEndDate).toInt()
            .takeUnless { it == 0 } ?: 1
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconValueDetailsItem(
            icon = Icons.Outlined.DateRange,
            value = when {
                !hasSearchedDateRange -> {
                    context.getString(R.string.lead_tenant_assistance_details_no_searched_date_range)
                }

                else -> {
                    context.resources.getQuantityString(
                        /* id = */ R.plurals.lead_tenant_assistance_details_searched_date_range,
                        /* quantity = */ dateRangeDaysCount,
                        searchedStartDate.dayOfMonth.formatToTwoDigitsDateTime(),
                        searchedStartDate.monthValue.formatToTwoDigitsDateTime(),
                        searchedStartDate.year,
                        searchedEndDate.dayOfMonth.formatToTwoDigitsDateTime(),
                        searchedEndDate.monthValue.formatToTwoDigitsDateTime(),
                        searchedEndDate.year,
                        dateRangeDaysCount,
                    )
                }
            },
            isEmptyText = !hasSearchedDateRange,
            modifier = modifier
        )
        if (hasFlexibilityForPeriod) {
            Spacer(modifier = Modifier.width(8.dp))
            Card(
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = modifier
            ) {
                Text(
                    text = stringResource(R.string.lead_tenant_assistance_details_has_flexibility_for_period_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MaxAcceptedNightlyRateDetailsItem(
    maxAcceptedNightlyRate: BigDecimal?,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = Icons.Outlined.AttachMoney,
        value = when {
            maxAcceptedNightlyRate == null -> {
                stringResource(R.string.lead_tenant_assistance_details_no_max_accepted_nighly_rate)
            }

            else -> {
                stringResource(
                    R.string.lead_tenant_assistance_details_max_accepted_nighly_rate,
                    maxAcceptedNightlyRate
                        .toString()
                        .replace(".", ","),
                )
            }
        },
        isEmptyText = maxAcceptedNightlyRate == null,
        modifier = modifier
    )
}

@Composable
fun GuestsDetailsItem(
    qtAdultGuests: Int,
    qtKidGuests: Int,
    qtBabyGuests: Int,
    qtAnimalGuests: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val guestsCount = remember(qtAdultGuests, qtKidGuests, qtBabyGuests, qtAnimalGuests) {
        qtAdultGuests + qtKidGuests + qtBabyGuests + qtAnimalGuests
    }

    val guestsText = remember(qtAdultGuests, qtKidGuests, qtBabyGuests, qtAnimalGuests) {
        val qtAdultPlural = context.resources.getQuantityString(
            R.plurals.lead_tenant_assistance_details_adult_guests, qtAdultGuests, qtAdultGuests
        )
        val qtKidPlural = context.resources.getQuantityString(
            R.plurals.lead_tenant_assistance_details_kid_guests, qtKidGuests, qtKidGuests
        )
        val qtBabyPlural = context.resources.getQuantityString(
            R.plurals.lead_tenant_assistance_details_baby_guests, qtBabyGuests, qtBabyGuests
        )
        val qtAnimalPlural = context.resources.getQuantityString(
            R.plurals.lead_tenant_assistance_details_animal_guests, qtAnimalGuests, qtAnimalGuests
        )

        persistentListOf(
            if (qtAdultGuests == 0) "" else qtAdultPlural,
            if (qtKidGuests == 0) "" else qtKidPlural,
            if (qtBabyGuests == 0) "" else qtBabyPlural,
            if (qtAnimalGuests == 0) "" else qtAnimalPlural,
        )
            .filter { it.isNotEmpty() }
            .run {
                if (isEmpty()) return@run ""
                if (size == 1) return@run first()

                val subList = subList(0, size - 1)
                "${subList.joinToString(separator = ", ")} ${context.getString(R.string.last_separator_label)} ${last()}"
            }
    }

    IconValueDetailsItem(
        icon = Icons.Outlined.FamilyRestroom,
        value = when {
            guestsCount > 0 -> guestsText
            else -> stringResource(R.string.lead_tenant_assistance_details_no_guests)
        },
        modifier = modifier
    )
}
