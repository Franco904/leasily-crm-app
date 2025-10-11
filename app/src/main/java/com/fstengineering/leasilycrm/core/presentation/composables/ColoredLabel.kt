package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ColoredLabel(
    label: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
    mustEllipsizeText: Boolean = false,
) {
    val density = LocalDensity.current

    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    var labelPosition by remember { mutableStateOf(Offset.Zero) }
    var labelSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .offset(x = if (color != null) 0.dp else 2.dp)
            .onGloballyPositioned { coords ->
                labelPosition = coords.positionInWindow()
                labelSize = coords.size
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    isDropdownMenuVisible = true
                }
            }
    ) {
        if (color != null) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        } else {
            UnavailablePeriodDateCircle(sizeDp = 20.dp)
        }
        Spacer(modifier = Modifier.width(if (color != null) 8.dp else 10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (mustEllipsizeText) 1 else Int.MAX_VALUE,
            overflow = if (mustEllipsizeText) TextOverflow.Ellipsis else TextOverflow.Visible,
        )
    }

    if (onClick != null) {
        val menuOffset = with(density) {
            val dateCenterX = labelPosition.x + (labelSize.width / 2)

            DpOffset(
                x = (dateCenterX.toDp() - 64.dp) / 2,
                y = 0.dp,
            )
        }

        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
        ) {
            AppMoreOptionsDropdownMenu(
                options = persistentListOf(
                    MoreOptionsMenuOption(
                        iconDrawable = R.drawable.calendar_view,
                        text = stringResource(R.string.view_property_stay_action_button_text),
                        onClick = onClick,
                    ),
                ),
                isMenuExpanded = isDropdownMenuVisible,
                onMenuExpanded = { isDropdownMenuVisible = it },
                offset = menuOffset,
            )
        }
    }
}
