package com.fstengineering.leasilycrm.assistanceRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.AssistanceObservationsFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.GuestsCountFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.MaxAcceptedNightlyRateFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.PreferredCharacteristicsCountFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.PreferredOtherCharacteristicsFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.PreferredSeaProximityTypeFormField
import com.fstengineering.leasilycrm.assistanceRegistration.composables.fields.SearchedStayDateRangeFormField
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.centerVisibleLazyItemByKey
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DateRangePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.core.presentation.utils.timestampToLocalDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationFormSectionStep1(
    assistanceForm: Assistance,
    onAssistanceFormChange: (Assistance) -> Unit,
    onAddSearchedNeighborhood: () -> Unit,
    onEditSearchedNeighborhood: (AssistanceSearchedNeighborhood) -> Unit,
    onDeleteSearchedNeighborhood: (AssistanceSearchedNeighborhood) -> Unit,
    seaProximityTypes: ImmutableList<PropertySeaProximityType>,
    assistancePropertyCharacteristics: ImmutableList<AssistanceCharacteristic>,
    formErrors: ImmutableMap<AssistanceStep1Field, Int?>,
    scrollToFieldFlow: Flow<AssistanceStep1Field>,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val lazyListState = rememberLazyListState()

    val bringIntoViewRequesters = remember {
        AssistanceStep1Field.entries.associateWith { BringIntoViewRequester() }
    }

    val initialDateSelection = remember {
        val calendar = Calendar.getInstance()

        Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            (calendar.get(Calendar.MONTH) + 1),
            calendar.get(Calendar.YEAR)
        )
    }

    var maxAcceptedNightlyRateInput by rememberSaveable(assistanceForm.maxAcceptedNightlyRate) {
        mutableStateOf(assistanceForm.maxAcceptedNightlyRate?.toString() ?: "")
    }

    val assistanceNotDeletedSearchedNeighborhoods = remember(assistanceForm) {
        assistanceForm.searchedNeighborhoods
            .filter { !it.isDeleted }
            .toPersistentList()
    }

    var mustShowDateRangePickerDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scrollToFieldFlow.collect { fieldToScroll ->
            bringIntoViewRequesters[fieldToScroll]?.bringIntoView()

            centerVisibleLazyItemByKey(
                lazyListState = lazyListState,
                key = fieldToScroll,
                fallbackIndex = fieldToScroll.scrollIndex,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_step_1_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                SearchedStayDateRangeFormField(
                    searchedStartDate = assistanceForm.searchedStartDate,
                    searchedEndDate = assistanceForm.searchedEndDate,
                    onTap = { mustShowDateRangePickerDialog = true },
                )
            }
            item {
                AppCheckbox(
                    label = stringResource(R.string.assistance_registration_customer_has_flexibility_body_text),
                    isChecked = assistanceForm.hasFlexibilityForPeriod,
                    onCheckedChange = {
                        onAssistanceFormChange(assistanceForm.copy(hasFlexibilityForPeriod = it))
                    },
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                MaxAcceptedNightlyRateFormField(
                    maxAcceptedNightlyRate = maxAcceptedNightlyRateInput,
                    onMaxAcceptedNightlyRateChange = { maxAcceptedNightlyRateInput = it }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_guests_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_guests_helper_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                GuestsCountFormField(
                    assistanceForm = assistanceForm,
                    onAssistanceFormChange = onAssistanceFormChange,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_basic_preferences_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                PreferredCharacteristicsCountFormField(
                    assistanceForm = assistanceForm,
                    onAssistanceFormChange = onAssistanceFormChange,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_sea_proximity_type_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item(key = AssistanceStep1Field.DistanceToSea) {
                Box(
                    modifier = Modifier
                        .bringIntoViewRequester(
                            bringIntoViewRequesters.getValue(AssistanceStep1Field.DistanceToSea),
                        )
                ) {
                    PreferredSeaProximityTypeFormField(
                        assistanceForm = assistanceForm,
                        onAssistanceFormChange = onAssistanceFormChange,
                        distanceToSeaError = formErrors[AssistanceStep1Field.DistanceToSea],
                        seaProximityTypes = seaProximityTypes,
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.assistance_registration_property_location_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                    )
                    TextButton(onClick = onAddSearchedNeighborhood) {
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
            if (assistanceNotDeletedSearchedNeighborhoods.isEmpty()) {
                item { AssistanceSearchedNeighborhoodsEmptySection() }
            } else {
                itemsIndexed(
                    items = assistanceNotDeletedSearchedNeighborhoods,
                    key = { i, searchedNeighborhood ->
                        searchedNeighborhood.id ?: "${searchedNeighborhood.neighborhood}$i"
                    },
                ) { i, searchedNeighborhood ->
                    if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                    AssistanceSearchedNeighborhoodCard(
                        searchedNeighborhood = searchedNeighborhood,
                        onEdit = { onEditSearchedNeighborhood(searchedNeighborhood) },
                        onDelete = { onDeleteSearchedNeighborhood(searchedNeighborhood) },
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = stringResource(R.string.assistance_registration_other_preferences_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                PreferredOtherCharacteristicsFormField(
                    assistanceForm = assistanceForm,
                    onAssistanceFormChange = onAssistanceFormChange,
                    assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HorizontalDivider() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                AssistanceObservationsFormField(
                    value = assistanceForm.observations,
                    onValueChange = {
                        onAssistanceFormChange(assistanceForm.copy(observations = it))
                    },
                )
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
                NextStepButton(onNext = {
                    val maxAcceptedNightlyRate = maxAcceptedNightlyRateInput
                        .replace(',', '.').toBigDecimalOrNull()
                    onAssistanceFormChange(assistanceForm.copy(maxAcceptedNightlyRate = maxAcceptedNightlyRate))

                    onNext()
                })
            }
        }
    }
    if (mustShowDateRangePickerDialog) {
        val (initialDayOfMonth, initialMonth, initialYear) = initialDateSelection

        DateRangePickerDialog(
            initialDisplayedMonthMillis = getMonthTimestamp(
                month = initialMonth,
                year = initialYear,
            ),
            selectableDates = getSelectableDates(
                initialDayOfMonth = initialDayOfMonth,
                initialMonth = initialMonth,
                initialYear = initialYear,
                alreadySelectedDates = persistentSetOf(),
            ),
            onDateRangeSelected = { (startDateTimestamp, endDateTimestamp) ->
                onAssistanceFormChange(
                    assistanceForm.copy(
                        searchedStartDate = startDateTimestamp.timestampToLocalDate(),
                        searchedEndDate = endDateTimestamp.timestampToLocalDate(),
                    )
                )

                mustShowDateRangePickerDialog = false
            },
            isDateSelectionEmpty = true,
            onDismiss = { mustShowDateRangePickerDialog = false },
        )
    }
}
