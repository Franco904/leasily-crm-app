package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceRegistrationStep2ListState
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceSentPropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.CancelOutlinedButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.models.PropertyUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationFormSectionStep2(
    listStatus: AssistanceRegistrationStep2ListState,
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    selectableProperties: ImmutableList<PropertyUiState>,
    onAddSentProperty: () -> Unit,
    isSearchingPropertyToSend: Boolean,
    onBack: () -> Unit,
    onCancelPropertySelection: () -> Unit,
    onNext: () -> Unit,
    onSelectProperties: (ImmutableList<PropertyUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val lazyListState = rememberLazyListState()

    var assistanceSentProperties by remember(assistanceForm.sentProperties) {
        mutableStateOf(assistanceForm.sentProperties.toPersistentList())
    }

    val isAddingSentProperty = remember(listStatus) {
        listStatus == AssistanceRegistrationStep2ListState.AddingSentProperties
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedContent(
            targetState = listStatus,
        ) { listStatus ->
            if (listStatus == AssistanceRegistrationStep2ListState.Empty) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    PropertiesEmptySection(isSearchingProperty = isSearchingPropertyToSend)
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    when (listStatus) {
                        AssistanceRegistrationStep2ListState.ShowingSelectedSentProperties -> {
                            showingSelectedSentPropertiesList(
                                alreadySelectedProperties = assistanceSentProperties,
                                onAddSentProperty = onAddSentProperty,
                            )
                        }

                        AssistanceRegistrationStep2ListState.AddingSentProperties -> {
                            addingSentPropertiesList(
                                selectableProperties = selectableProperties,
                                alreadySelectedPropertiesIds = assistanceSentProperties
                                    .map { it.id }
                                    .toPersistentList(),
                                onSentPropertySelect = { property ->
                                    onSentPropertySelect(
                                        selectedProperty = property,
                                        assistanceSentProperties = assistanceSentProperties,
                                        onNewSelectedProperties = {
                                            assistanceSentProperties = it
                                        },
                                    )
                                },
                            )
                        }

                        else -> {}
                    }
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }
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

                if (isAddingSentProperty) CancelOutlinedButton(onCancel = onCancelPropertySelection)
                else BackStepButton(onBack = onBack)

                Spacer(modifier = Modifier.width(8.dp))

                if (isAddingSentProperty) {
                    ApplySelectionButton(
                        onAdd = { onSelectProperties(assistanceSentProperties) },
                    )
                } else {
                    NextStepButton(onNext = onNext)
                }
            }
        }
    }
}

private fun LazyListScope.addingSentPropertiesList(
    selectableProperties: ImmutableList<PropertyUiState>,
    alreadySelectedPropertiesIds: ImmutableList<Long>,
    onSentPropertySelect: (PropertyUiState) -> Unit,
) {
    val selectedPropertiesCount = alreadySelectedPropertiesIds.size

    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Text(
            text = when {
                selectedPropertiesCount == 0 -> {
                    stringResource(R.string.assistance_registration_no_selected_sent_properties)
                }

                else -> {
                    pluralStringResource(
                        R.plurals.assistance_registration_number_of_selected_sent_properties,
                        selectedPropertiesCount,
                        selectedPropertiesCount,
                    )
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
    item { Spacer(modifier = Modifier.height(16.dp)) }
    itemsIndexed(
        items = selectableProperties,
        key = { _, property -> property.id },
        contentType = { _, entryType -> entryType },
    ) { _, property ->
        PropertyCard(
            property = property,
            isSelected = property.id in alreadySelectedPropertiesIds,
            onCardClick = { onSentPropertySelect(property) },
            hasMoreOptionsMenu = false,
            hasCharacteristicsChipsRow = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun LazyListScope.showingSelectedSentPropertiesList(
    alreadySelectedProperties: ImmutableList<PropertyUiState>,
    onAddSentProperty: () -> Unit,
) {
    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.assistance_registration_step_2_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            TextButton(onClick = onAddSentProperty) {
                Icon(
                    imageVector = when {
                        alreadySelectedProperties.isEmpty() -> Icons.Default.Add
                        else -> Icons.Outlined.Edit
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        alreadySelectedProperties.isEmpty() -> stringResource(R.string.add_action_button_text)
                        else -> stringResource(R.string.edit_action_button_text)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
    if (alreadySelectedProperties.isEmpty()) {
        item { AssistanceSentPropertiesEmptySection() }
    } else {
        itemsIndexed(
            items = alreadySelectedProperties,
            key = { i, entry -> "${entry.id}-$i" },
            contentType = { _, entryType -> entryType },
        ) { i, selectedProperty ->
            if (i > 0) Spacer(modifier = Modifier.height(8.dp))
            PropertyCard(
                property = selectedProperty,
                hasMoreOptionsMenu = false,
                hasCharacteristicsChipsRow = false,
            )
        }
    }
}

private fun onSentPropertySelect(
    selectedProperty: PropertyUiState,
    assistanceSentProperties: PersistentList<PropertyUiState>,
    onNewSelectedProperties: (PersistentList<PropertyUiState>) -> Unit,
) {
    val assistanceSentPropertiesIds = assistanceSentProperties.map { it.id }

    val newAssistanceSentProperties = if (selectedProperty.id in assistanceSentPropertiesIds) {
        val existingSentProperty = assistanceSentProperties
            .find { it.id == selectedProperty.id } ?: return

        assistanceSentProperties.remove(existingSentProperty)
    } else {
        assistanceSentProperties.add(selectedProperty)
    }

    onNewSelectedProperties(newAssistanceSentProperties)
}
