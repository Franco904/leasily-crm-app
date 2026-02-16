package com.fstengineering.leasilycrm.assistanceRegistration.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.AssistanceInteractionDateFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.AssistanceInteractionDescriptionFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.AssistanceInteractionTitleFormField
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceInteractionField
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DatePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentSetOf
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceInteractionRegistrationBottomsheet(
    sheetState: SheetState,
    interactionForm: AssistanceInteraction,
    onInterationFormChange: (AssistanceInteraction) -> Unit,
    formErrors: ImmutableMap<AssistanceInteractionField, Int?>,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    val interactionTitleFieldFocusRequester = remember { FocusRequester() }
    val interactionDescriptionFieldFocusRequester = remember { FocusRequester() }

    val initialDateSelection = remember {
        val calendar = Calendar.getInstance()

        Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            (calendar.get(Calendar.MONTH) + 1),
            calendar.get(Calendar.YEAR)
        )
    }

    var mustShowDatePickerDialog by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                            interactionForm.id == null -> R.string.assistance_registration_interaction_registration_title_create
                            else -> R.string.assistance_registration_interaction_registration_title_edit
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
                OutlinedButton(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    onClick = onSave,
                ) {
                    Text(
                        text = stringResource(R.string.save_action_button_text),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AssistanceInteractionDateFormField(
                interactionDate = interactionForm.interactionDate,
                error = formErrors[AssistanceInteractionField.InteractionDate],
                onTap = { mustShowDatePickerDialog = true },
            )
            Spacer(modifier = Modifier.height(16.dp))
            AssistanceInteractionTitleFormField(
                value = interactionForm.title,
                onValueChange = { onInterationFormChange(interactionForm.copy(title = it)) },
                error = formErrors[AssistanceInteractionField.Title],
                onDone = { interactionDescriptionFieldFocusRequester.requestFocus() },
                fieldFocusRequester = interactionTitleFieldFocusRequester,
            )
            Spacer(modifier = Modifier.height(16.dp))
            AssistanceInteractionDescriptionFormField(
                value = interactionForm.description,
                onValueChange = { onInterationFormChange(interactionForm.copy(description = it)) },
                fieldFocusRequester = interactionDescriptionFieldFocusRequester,
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
                onInterationFormChange(
                    interactionForm.copy(interactionDate = selectedDate)
                )

                mustShowDatePickerDialog = false

                interactionTitleFieldFocusRequester.requestFocus()
            },
            onDismiss = { mustShowDatePickerDialog = false },
        )
    }
}
