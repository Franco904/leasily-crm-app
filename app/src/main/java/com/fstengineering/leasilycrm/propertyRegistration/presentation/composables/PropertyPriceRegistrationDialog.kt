package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimalOrNull
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DateRangePickerDialog
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.PriceAmountFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields.PricePeriodFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyPriceDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyPriceField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyPriceRegistrationDialog(
    priceForm: PropertyPriceDraftFormUiState,
    onPriceFormChange: (PropertyPriceDraftFormUiState) -> Unit,
    currentPrices: ImmutableList<PropertyPriceUiState>,
    formErrors: ImmutableMap<PropertyPriceField, Int?>,
    isEditing: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val priceAmountFieldFocusRequester = remember { FocusRequester() }

    val initialDateSelection = remember {
        val calendar = Calendar.getInstance()

        Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            (calendar.get(Calendar.MONTH) + 1),
            calendar.get(Calendar.YEAR)
        )
    }

    var priceAmountInput by rememberSaveable(priceForm.amount) {
        mutableStateOf(priceForm.amount.toString())
    }

    var mustShowDateRangePickerDialog by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(
                text = stringResource(
                    when {
                        isEditing -> R.string.property_registration_date_range_price_dialog_title_edit
                        else -> R.string.property_registration_date_range_price_dialog_title_create
                    }
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                PricePeriodFormField(
                    priceStartDate = priceForm.startDate,
                    priceEndDate = priceForm.endDate,
                    error = formErrors[PropertyPriceField.Period],
                    onTap = { mustShowDateRangePickerDialog = true },
                )
                Spacer(modifier = Modifier.height(16.dp))
                PriceAmountFormField(
                    priceAmount = priceAmountInput,
                    onPriceAmountChange = {
                        priceAmountInput = it
                    },
                    error = formErrors[PropertyPriceField.Amount],
                    priceAmountFieldFocusRequester = priceAmountFieldFocusRequester,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val priceAmount = priceAmountInput
                        .replace(',', '.').toDecimalOrNull()
                    onPriceFormChange(priceForm.copy(amount = priceAmount))

                    onSave()
                },
            ) {
                Text(
                    text = stringResource(
                        if (isEditing) R.string.edit_action_button_text
                        else R.string.save_action_button_text,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )

    if (mustShowDateRangePickerDialog) {
        val (initialDayOfMonth, initialMonth, initialYear) = initialDateSelection

        val alreadySelectedDates = remember(isEditing, priceForm) {
            currentPrices
                .getPropertyPriceAlreadySelectedDates(
                    isEditing = isEditing,
                    priceFormStartDate = priceForm.startDate,
                )
        }

        DateRangePickerDialog(
            initialDisplayedMonthMillis = getMonthTimestamp(
                month = initialMonth,
                year = initialYear,
            ),
            selectableDates = getSelectableDates(
                initialDayOfMonth = initialDayOfMonth,
                initialMonth = initialMonth,
                initialYear = initialYear,
                fixedNonSelectableDates = alreadySelectedDates,
            ),
            onDateRangeSelected = { (startDateTimestamp, endDateTimestamp) ->
                onPriceFormChange(
                    priceForm.copy(
                        startDate = startDateTimestamp.toDate(),
                        endDate = endDateTimestamp.toDate(),
                    )
                )

                mustShowDateRangePickerDialog = false

                if (priceForm.amount == null) {
                    priceAmountFieldFocusRequester.requestFocus()
                }
            },
            isDateSelectionEmpty = currentPrices.isEmpty(),
            onDismiss = { mustShowDateRangePickerDialog = false },
        )
    }
}

private fun ImmutableList<PropertyPriceUiState>.getPropertyPriceAlreadySelectedDates(
    isEditing: Boolean,
    priceFormStartDate: AppDate?,
): ImmutableSet<AppDate> {
    return this.asSequence()
        .filter { !isEditing && it.startDate != priceFormStartDate }
        .flatMap { price ->
            val startDate = price.startDate
            val endDate = price.endDate

            generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { it <= endDate }
        }
        .toPersistentSet()
}
