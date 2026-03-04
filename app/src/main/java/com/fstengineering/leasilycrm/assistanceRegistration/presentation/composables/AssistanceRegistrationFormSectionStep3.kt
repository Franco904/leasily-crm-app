package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionDraftFormUiState
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceInteractionsEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.SavePrimaryButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.AssistanceInteractionCard
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationFormSectionStep3(
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    onAddInteraction: () -> Unit,
    onViewInteractionDetails: (AssistanceInteractionDraftFormUiState.Loaded) -> Unit,
    onEditInteraction: (AssistanceInteractionDraftFormUiState.Loaded) -> Unit,
    onDeleteInteraction: (AssistanceInteractionDraftFormUiState.Loaded) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val lazyListState = rememberLazyListState()

    val assistanceInteractions = remember(assistanceForm.interactions) {
        assistanceForm.interactions
            .sortedByDescending { it.date }
            .toPersistentList()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.assistance_registration_step_3_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                    )
                    TextButton(onClick = onAddInteraction) {
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
            if (assistanceInteractions.isEmpty()) {
                item { AssistanceInteractionsEmptySection() }
            } else {
                itemsIndexed(
                    items = assistanceInteractions,
                    key = { i, interaction -> interaction.id ?: "${interaction.id}$i" },
                    contentType = { _, entryType -> entryType },
                ) { i, interaction ->
                    if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                    AssistanceInteractionCard(
                        interaction = interaction.toCardDetails(),
                        onViewInteractionDetails = {
                            onViewInteractionDetails(interaction)
                        },
                        onEditInteraction = { onEditInteraction(interaction) },
                        onDeleteInteraction = { onDeleteInteraction(interaction) },
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
                SavePrimaryButton(onSave = onSave)
            }
        }
    }
}
