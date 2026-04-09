package com.fstengineering.leasilycrm.propertyDetails.presentation.composables

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
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyPricesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyPriceCard
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import java.util.Calendar

@Composable
fun PropertyDetailsFinancialPage(
    property: PropertyUiState.Loaded,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val calendar = remember { Calendar.getInstance() }

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
            calendar = calendar,
            prices = property.prices,
        )
        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

fun LazyListScope.pricesDetailsItems(
    context: Context,
    calendar: Calendar,
    prices: ImmutableMap<PriceMonthYearUiState, ImmutableList<PropertyPriceUiState>>,
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
            items = prices,
            key = { _, entry -> entry.key },
            contentType = { _, entryType -> entryType },
        ) { i, (monthYear, prices) ->
            val isCurrentYear = monthYear.year == calendar.get(Calendar.YEAR)

            val monthLabel = context.getMonthLabelByNumber(monthNumber = monthYear.month)
            val monthYearLabel = "$monthLabel ${if (isCurrentYear) "" else monthYear.year}"

            if (i > 0) Spacer(modifier = Modifier.height(8.dp))
            PropertyPriceCard(
                monthYearLabel = monthYearLabel,
                monthPrices = prices,
                hasMoreOptionsMenu = false,
            )
        }
    }
}
