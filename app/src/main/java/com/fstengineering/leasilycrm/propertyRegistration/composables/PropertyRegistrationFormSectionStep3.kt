package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyPricesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyPriceCard
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthLabelByNumber
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.CleaningServiceTaxFormField
import kotlinx.collections.immutable.toImmutableList
import okhttp3.internal.toImmutableMap
import java.util.Calendar

@Composable
fun PropertyRegistrationFormSectionStep3(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    onAddPropertyPrice: () -> Unit,
    onEditPropertyPrice: (PropertyPrice) -> Unit,
    onDeletePropertyPrice: (PropertyPrice) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val calendar = remember { Calendar.getInstance() }

    val context = LocalContext.current
    val outlineColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = stringResource(R.string.property_registration_step_3_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                CleaningServiceTaxFormField(
                    cleaningServiceTax = propertyForm.cleaningServiceTax,
                    onCleaningServiceTaxChange = {
                        onPropertyFormChange(
                            propertyForm.copy(
                                cleaningServiceTax = it,
                                hasCleaningServiceIncluded = true,
                            )
                        )
                    },
                )
            }
            item {
                AppCheckbox(
                    label = stringResource(R.string.property_registration_does_not_include_cleaning_service_body_text),
                    isChecked = !(propertyForm.hasCleaningServiceIncluded ?: true),
                    onCheckedChange = { newNotHasCleaningServiceIncluded ->
                        onPropertyFormChange(
                            propertyForm.copy(hasCleaningServiceIncluded = !newNotHasCleaningServiceIncluded)
                        )
                    },
                    isEnabled = propertyForm.cleaningServiceTax?.toString().isNullOrBlank(),
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.property_registration_daily_price_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                    )
                    TextButton(onClick = onAddPropertyPrice) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.add_action_button_text),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            if (propertyForm.prices.isEmpty()) {
                item { PropertyPricesEmptySection() }
            } else {
                itemsIndexed(
                    items = propertyForm.prices
                        .sortedBy { it.startDate }
                        .groupBy { price ->
                            CalendarMonthYear(
                                id = price.startDate!!.monthValue + price.startDate.year,
                                month = price.startDate.monthValue,
                                year = price.startDate.year,
                            )
                        }
                        .toImmutableMap(),
                    key = { _, entry -> entry.key.id },
                    contentType = { _, entryType -> entryType },
                ) { i, monthPrices ->
                    val monthLabel =
                        context.getMonthLabelByNumber(monthNumber = monthPrices.key.month)
                    val isCurrentYear = monthPrices.key.year == calendar.get(Calendar.YEAR)

                    val finalMonthLabel =
                        "$monthLabel ${if (isCurrentYear) "" else monthPrices.key.year}"

                    if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                    PropertyPriceCard(
                        monthLabel = finalMonthLabel,
                        monthPrices = monthPrices.value.toImmutableList(),
                        onEditPropertyPrice = onEditPropertyPrice,
                        onDeletePropertyPrice = onDeletePropertyPrice,
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .drawBehind {
                    drawLine(
                        color = outlineColor,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = size.width, y = 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                BackStepButton(onBack = onBack)
                Spacer(modifier = Modifier.width(8.dp))
                NextStepButton(onNext = {
                    if (propertyForm.cleaningServiceTax?.toString().isNullOrBlank()) {
                        onPropertyFormChange(propertyForm.copy(hasCleaningServiceIncluded = false))
                    }

                    onNext()
                })
            }
        }
    }
}
