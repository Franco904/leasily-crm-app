package com.fstengineering.leasilycrm.clienteDetails.presentation.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoLastInteracaoDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoLastSentImovelDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoLastUpdateDateDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoObservationsDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoPreferredOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.AtendimentoSearchedNeighborhoodsDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.PreferredSeaProximityTypeDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.AtendimentoUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.InteracaoUiState
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppTonalButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetailsCurrentAtendimentoPage(
    currentAtendimento: AtendimentoUiState?,
    onRegisterAtendimento: () -> Unit,
    onEditAtendimento: () -> Unit,
    onDeleteAtendimento: () -> Unit,
    onViewAllSentImoveis: () -> Unit,
    onViewAllInteracoes: () -> Unit,
    onViewSentImovelDetails: (Long) -> Unit,
    onViewInteracaoDetails: (InteracaoUiState) -> Unit,
    isViewingAllSentImoveis: Boolean,
    isViewingAllInterations: Boolean,
    isBringIntoViewEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val sentImoveisBringIntoViewRequester = remember { BringIntoViewRequester() }
    val interacoesBringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(isViewingAllSentImoveis) {
        if (isBringIntoViewEnabled && !isViewingAllSentImoveis) {
            sentImoveisBringIntoViewRequester.bringIntoView()
        }
    }

    LaunchedEffect(isViewingAllInterations) {
        if (isBringIntoViewEnabled && !isViewingAllInterations) {
            interacoesBringIntoViewRequester.bringIntoView()
        }
    }

    AnimatedContent(
        targetState = currentAtendimento != null,
    ) { hasCurrentAtendimento ->
        if (!hasCurrentAtendimento) {
            AtendimentoEmptySection(onRegisterAtendimento = onRegisterAtendimento)
        } else {
            if (currentAtendimento == null) return@AnimatedContent

            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    AppTonalButton(
                        icon = Icons.Outlined.Edit,
                        label = stringResource(R.string.edit_action_button_text),
                        onClick = onEditAtendimento,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppTonalButton(
                        icon = Icons.Outlined.Delete,
                        label = stringResource(R.string.delete_action_button_text),
                        onClick = onDeleteAtendimento,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoLastUpdateDateDetailsItem(
                    updateDateTime = currentAtendimento.updateDateTime,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoQuantityCharacteristicsDetailsItems(atendimento = currentAtendimento)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                PreferredSeaProximityTypeDetailsItem(
                    seaProximityTypeId = currentAtendimento.preferredSeaProximityTypeId,
                    distanceToSea = currentAtendimento.distanceToSea,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoSearchedNeighborhoodsDetailsItem(
                    searchedNeighborhoods = currentAtendimento.preferredNeighborhoods,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoPreferredOtherCharacteristicsDetailsItems(
                    checkedCharacteristics = currentAtendimento.preferredImovelCharacteristics,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoObservationsDetailsItem(observations = currentAtendimento.observations)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoLastSentImovelDetailsItem(
                    lastSentImovel = currentAtendimento.lastSentImovel,
                    onViewAll = onViewAllSentImoveis,
                    onViewSentImovelDetails = {
                        onViewSentImovelDetails(currentAtendimento.lastSentImovel!!.id)
                    },
                    modifier = Modifier
                        .bringIntoViewRequester(sentImoveisBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AtendimentoLastInteracaoDetailsItem(
                    lastInteracao = currentAtendimento.lastInteracao,
                    onViewAll = onViewAllInteracoes,
                    onViewInteracaoDetails = {
                        onViewInteracaoDetails(currentAtendimento.lastInteracao!!)
                    },
                    modifier = Modifier
                        .bringIntoViewRequester(interacoesBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
