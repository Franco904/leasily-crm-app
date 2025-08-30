package com.fstengineering.leasilycrm.profile.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.profile.models.UserUiState

@Composable
fun ProfileDataSection(
    userUiState: UserUiState,
    onSignOut: () -> Unit,
    onEditUser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = userUiState.profilePictureUrl,
        contentDescription = null,
        modifier = modifier
            .size(112.dp)
            .clip(CircleShape)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = userUiState.name ?: "-",
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = if (userUiState.creciNumber != null) "CRECI - ${userUiState.creciNumber}" else stringResource(
            R.string.profile_creci_blank_body_text
        ),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Medium,
            fontStyle = if (userUiState.creciNumber != null) FontStyle.Normal else FontStyle.Italic,
        ),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row {
        OutlinedButton(onClick = onSignOut) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.sign_out_action_button_text),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        FilledTonalButton(onClick = onEditUser) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.edit_action_button_text),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
