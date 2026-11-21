package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables

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
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoRegistrationStep2ListState
import com.fstengineering.leasilycrm.core.presentation.composables.ImoveisEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.ImoveisEnviadosEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.CancelOutlinedButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.NextStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.ImovelCard
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtendimentoRegistrationFormSectionStep2(
    listStatus: AtendimentoRegistrationStep2ListState,
    atendimentoForm: AtendimentoDraftFormUiState.Loaded,
    selectableImoveis: ImmutableList<ImovelCardUiState>,
    onAddImovelEnviado: () -> Unit,
    isSearchingImovelToSend: Boolean,
    onBack: () -> Unit,
    onCancelImovelSelection: () -> Unit,
    onNext: () -> Unit,
    onSelectImoveis: (ImmutableList<ImovelCardUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val lazyListState = rememberLazyListState()

    var imoveisEnviados by remember(atendimentoForm.imoveisEnviados) {
        mutableStateOf(atendimentoForm.imoveisEnviados.toPersistentList())
    }

    val isAddingImovelEnviado = remember(listStatus) {
        listStatus == AtendimentoRegistrationStep2ListState.AddingImoveisEnviados
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedContent(
            targetState = listStatus,
        ) { listStatus ->
            if (listStatus == AtendimentoRegistrationStep2ListState.Empty) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ImoveisEmptySection(isSearchingImovel = isSearchingImovelToSend)
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    when (listStatus) {
                        AtendimentoRegistrationStep2ListState.ShowingSelectedImoveisEnviados -> {
                            showingSelectedImoveisEnviadosList(
                                alreadySelectedImoveis = imoveisEnviados,
                                onAddImovelEnviado = onAddImovelEnviado,
                            )
                        }

                        AtendimentoRegistrationStep2ListState.AddingImoveisEnviados -> {
                            addingImoveisEnviadosList(
                                selectableImoveis = selectableImoveis,
                                alreadySelectedImoveisIds = imoveisEnviados
                                    .map { it.id }
                                    .toPersistentList(),
                                onImovelEnviadoSelect = { imovel ->
                                    onImovelEnviadoSelect(
                                        selectedImovel = imovel,
                                        imoveisEnviados = imoveisEnviados,
                                        onNewSelectedImoveis = {
                                            imoveisEnviados = it
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

                if (isAddingImovelEnviado) CancelOutlinedButton(onCancel = onCancelImovelSelection)
                else BackStepButton(onBack = onBack)

                Spacer(modifier = Modifier.width(8.dp))

                if (isAddingImovelEnviado) {
                    ApplySelectionButton(
                        onAdd = { onSelectImoveis(imoveisEnviados) },
                    )
                } else {
                    NextStepButton(onNext = onNext)
                }
            }
        }
    }
}

private fun LazyListScope.addingImoveisEnviadosList(
    selectableImoveis: ImmutableList<ImovelCardUiState>,
    alreadySelectedImoveisIds: ImmutableList<Long>,
    onImovelEnviadoSelect: (ImovelCardUiState) -> Unit,
) {
    val selectedImoveisCount = alreadySelectedImoveisIds.size

    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Text(
            text = when {
                selectedImoveisCount == 0 -> {
                    stringResource(R.string.assistance_registration_no_selected_sent_properties)
                }

                else -> {
                    pluralStringResource(
                        R.plurals.assistance_registration_number_of_selected_sent_properties,
                        selectedImoveisCount,
                        selectedImoveisCount,
                    )
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
    item { Spacer(modifier = Modifier.height(16.dp)) }
    itemsIndexed(
        items = selectableImoveis,
        key = { _, imovel -> imovel.id },
        contentType = { _, entryType -> entryType },
    ) { _, imovel ->
        ImovelCard(
            imovel = imovel,
            isSelected = imovel.id in alreadySelectedImoveisIds,
            onCardClick = { onImovelEnviadoSelect(imovel) },
            hasMoreOptionsMenu = false,
            hasCharacteristicsChipsRow = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun LazyListScope.showingSelectedImoveisEnviadosList(
    alreadySelectedImoveis: ImmutableList<ImovelCardUiState>,
    onAddImovelEnviado: () -> Unit,
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
            TextButton(onClick = onAddImovelEnviado) {
                Icon(
                    imageVector = when {
                        alreadySelectedImoveis.isEmpty() -> Icons.Default.Add
                        else -> Icons.Outlined.Edit
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        alreadySelectedImoveis.isEmpty() -> stringResource(R.string.add_action_button_text)
                        else -> stringResource(R.string.edit_action_button_text)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
    if (alreadySelectedImoveis.isEmpty()) {
        item { ImoveisEnviadosEmptySection() }
    } else {
        itemsIndexed(
            items = alreadySelectedImoveis,
            key = { i, entry -> "${entry.id}-$i" },
            contentType = { _, entryType -> entryType },
        ) { i, selectedImovel ->
            if (i > 0) Spacer(modifier = Modifier.height(8.dp))
            ImovelCard(
                imovel = selectedImovel,
                hasMoreOptionsMenu = false,
                hasCharacteristicsChipsRow = false,
            )
        }
    }
}

private fun onImovelEnviadoSelect(
    selectedImovel: ImovelCardUiState,
    imoveisEnviados: PersistentList<ImovelCardUiState>,
    onNewSelectedImoveis: (PersistentList<ImovelCardUiState>) -> Unit,
) {
    val imoveisEnviadosIds = imoveisEnviados.map { it.id }

    val newImoveisEnviados = if (selectedImovel.id in imoveisEnviadosIds) {
        val existingImovelEnviado = imoveisEnviados
            .find { it.id == selectedImovel.id } ?: return

        imoveisEnviados.remove(existingImovelEnviado)
    } else {
        imoveisEnviados.add(selectedImovel)
    }

    onNewSelectedImoveis(newImoveisEnviados)
}
