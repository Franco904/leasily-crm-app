package com.fstengineering.leasilycrm.propertyRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
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
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyPicturesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyPicturesRow
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.BackStepButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.SavePrimaryButton
import com.fstengineering.leasilycrm.propertyRegistration.composables.fields.PropertyObservationsFormField
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PropertyRegistrationFormSectionStep4(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    onAddPropertyPicture: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    val scrollState = rememberScrollState()

    val propertyNotDeletedFiles = remember(propertyForm) {
        propertyForm.files.filter { !it.isDeleted }.toPersistentList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.property_registration_step_4_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.property_registration_pictures_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
                TextButton(onClick = onAddPropertyPicture) {
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
            Spacer(modifier = Modifier.height(24.dp))
            if (propertyForm.files.isEmpty()) {
                PropertyPicturesEmptySection()
            } else {
                PropertyPicturesRow(
                    propertyNotDeletedPictures = propertyNotDeletedFiles,
                    propertyForm = propertyForm,
                    onPropertyFormChange = onPropertyFormChange,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            PropertyObservationsFormField(
                value = propertyForm.observations,
                onValueChange = { onPropertyFormChange(propertyForm.copy(observations = it)) },
            )
            Spacer(modifier = Modifier.height(120.dp))
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
