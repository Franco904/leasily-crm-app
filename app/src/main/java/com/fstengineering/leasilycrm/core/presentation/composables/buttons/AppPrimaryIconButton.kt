package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppPrimaryIconButton(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    var isBeingPressed by remember { mutableStateOf(false) }
    val cornerRadius: Dp by animateDpAsState(if (isBeingPressed) 12.dp else 128.dp)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            isBeingPressed = it is PressInteraction.Press
        }
    }

    FilledIconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
        )
    }
}
