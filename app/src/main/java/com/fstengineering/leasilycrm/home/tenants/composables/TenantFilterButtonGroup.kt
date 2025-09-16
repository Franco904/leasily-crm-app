package com.fstengineering.leasilycrm.home.tenants.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.home.tenants.models.TenantFilterType

@Composable
fun TenantFilterButtonGroup(
    selectedFilterType: TenantFilterType,
    onFilterTypeSelect: (TenantFilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            onClick = { onFilterTypeSelect(TenantFilterType.UpcomingStay) },
            color = when {
                selectedFilterType == TenantFilterType.UpcomingStay -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            },
            shape = when {
                selectedFilterType == TenantFilterType.UpcomingStay -> CircleShape
                else -> RoundedCornerShape(
                    topStart = CircleShape.topStart,
                    topEnd = MaterialTheme.shapes.small.topEnd,
                    bottomStart = CircleShape.bottomStart,
                    bottomEnd = MaterialTheme.shapes.small.bottomEnd,
                )
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(TenantFilterType.UpcomingStay.label),
                style = MaterialTheme.typography.labelLarge,
                color = when {
                    selectedFilterType == TenantFilterType.UpcomingStay -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Surface(
            onClick = { onFilterTypeSelect(TenantFilterType.Old) },
            color = when {
                selectedFilterType == TenantFilterType.Old -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            },
            shape = when {
                selectedFilterType == TenantFilterType.Old -> CircleShape
                else -> RoundedCornerShape(
                    topStart = MaterialTheme.shapes.small.topStart,
                    topEnd = CircleShape.topEnd,
                    bottomStart = MaterialTheme.shapes.small.bottomStart,
                    bottomEnd = CircleShape.bottomEnd,
                )
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(TenantFilterType.Old.label),
                style = MaterialTheme.typography.labelLarge,
                color = when {
                    selectedFilterType == TenantFilterType.Old -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }
    }
}
