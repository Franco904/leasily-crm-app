package com.fstengineering.leasilycrm.stayRegistration.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.toDecimalOrNull
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DatePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTransferAmountFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTransferDateFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTransferDescriptionFormField
import com.fstengineering.leasilycrm.stayRegistration.models.StayTransferField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentSetOf
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayTransferRegistrationBottomsheet(
    sheetState: SheetState,
    transferForm: StayTransfer,
    onTransferFormChange: (StayTransfer) -> Unit,
    formErrors: ImmutableMap<StayTransferField, Int?>,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    val transferAmountFieldFocusRequester = remember { FocusRequester() }
    val transferDescriptionFieldFocusRequester = remember { FocusRequester() }

    val initialDateSelection = remember {
        val calendar = Calendar.getInstance()

        Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            (calendar.get(Calendar.MONTH) + 1),
            calendar.get(Calendar.YEAR)
        )
    }

    var transferAmountInput by rememberSaveable(transferForm.transferAmount) {
        mutableStateOf(transferForm.transferAmount?.toString() ?: "")
    }

    var mustShowDatePickerDialog by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        when {
                            transferForm.id == null -> R.string.stay_registration_transfer_registration_title_create
                            else -> R.string.stay_registration_transfer_registration_title_edit
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
                OutlinedButton(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    onClick = {
                        val amount = transferAmountInput.replace(',', '.').toDecimalOrNull()
                        onTransferFormChange(transferForm.copy(transferAmount = amount))

                        onSave()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.save_action_button_text),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            StayTransferDateFormField(
                transferDate = transferForm.date,
                error = formErrors[StayTransferField.TransferDate],
                onTap = { mustShowDatePickerDialog = true },
            )
            Spacer(modifier = Modifier.height(16.dp))
            StayTransferAmountFormField(
                transferAmount = transferAmountInput,
                onTransferAmountChange = { transferAmountInput = it },
                error = formErrors[StayTransferField.TransferAmount],
                onDone = { transferDescriptionFieldFocusRequester.requestFocus() },
                fieldFocusRequester = transferAmountFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(16.dp))
            StayTransferDescriptionFormField(
                value = transferForm.transferDescription,
                onValueChange = { onTransferFormChange(transferForm.copy(transferDescription = it)) },
                fieldFocusRequester = transferDescriptionFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
    if (mustShowDatePickerDialog) {
        val (initialDayOfMonth, initialMonth, initialYear) = initialDateSelection

        DatePickerDialog(
            initialDisplayedMonthMillis = getMonthTimestamp(
                month = initialMonth,
                year = initialYear,
            ),
            selectableDates = getSelectableDates(
                initialDayOfMonth = initialDayOfMonth,
                initialMonth = initialMonth,
                initialYear = initialYear,
                forceBelow = true,
                alreadySelectedDates = persistentSetOf(),
            ),
            onDateSelected = { selectedDate ->
                onTransferFormChange(
                    transferForm.copy(transferDate = selectedDate)
                )

                mustShowDatePickerDialog = false

                transferAmountFieldFocusRequester.requestFocus()
            },
            onDismiss = { mustShowDatePickerDialog = false },
        )
    }
}
