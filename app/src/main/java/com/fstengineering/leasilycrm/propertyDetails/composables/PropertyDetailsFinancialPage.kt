package com.fstengineering.leasilycrm.propertyDetails.composables

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.entities.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyPricesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyPriceCard
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import okhttp3.internal.toImmutableMap
import java.util.Calendar

@Composable
fun PropertyDetailsFinancialPage(
    property: Property,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentCalendar = remember { Calendar.getInstance() }

    LazyColumn(
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            val cleaningServiceTaxFormatted = property.cleaningServiceTax
                .toString()
                .replace(".", ",")

            IconLabelValueDetailsItem(
                icon = Icons.Outlined.CleaningServices,
                label = stringResource(R.string.property_registration_cleaning_service_tax_label),
                value = when {
                    property.hasCleaningServiceIncluded == true -> "R$ $cleaningServiceTaxFormatted"
                    else -> stringResource(R.string.property_registration_does_not_include_cleaning_service_body_text)
                },
                isEmptyText = property.hasCleaningServiceIncluded != true,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        pricesDetailsItems(
            context = context,
            currentCalendar = currentCalendar,
            prices = property.prices,
        )
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

fun LazyListScope.pricesDetailsItems(
    context: Context,
    currentCalendar: Calendar,
    prices: ImmutableList<PropertyPrice>,
    modifier: Modifier = Modifier,
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Outlined.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.property_registration_daily_price_label),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
    if (prices.isEmpty()) {
        item { PropertyPricesEmptySection() }
    } else {
        itemsIndexed(
            items = prices
                .sortedBy { it.startDate?.date() }
                .groupBy { price ->
                    CalendarMonthYear(
                        month = price.startDate?.month ?: 0,
                        year = price.startDate?.year ?: 0,
                    )
                }
                .toImmutableMap(),
            key = { _, entry -> entry.key.id },
            contentType = { _, entryType -> entryType },
        ) { i, monthPrices ->
            val monthLabel =
                context.getMonthLabelByNumber(monthNumber = monthPrices.key.month)
            val isCurrentYear = monthPrices.key.year == currentCalendar.get(Calendar.YEAR)

            val finalMonthLabel =
                "$monthLabel ${if (isCurrentYear) "" else monthPrices.key.year}"

            if (i > 0) Spacer(modifier = Modifier.height(8.dp))
            PropertyPriceCard(
                monthLabel = finalMonthLabel,
                monthPrices = monthPrices.value.toImmutableList(),
                hasMoreOptionsMenu = false,
            )
        }
    }
}
