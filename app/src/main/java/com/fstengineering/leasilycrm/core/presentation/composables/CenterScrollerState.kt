package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow

@Composable
fun rememberCenterScroller(
    scrollState: ScrollState,
): suspend (LayoutCoordinates?, LayoutCoordinates?) -> Unit {
    return remember(scrollState) {
        { scrollContainer, target ->
            if (scrollContainer != null && target != null) {
                val scrollContainerTop = scrollContainer.positionInWindow().y
                val targetTop = target.positionInWindow().y

                val scrollContainerH = scrollContainer.size.height.toFloat()
                val targetH = target.size.height.toFloat()

                val delta = (targetTop - scrollContainerTop) - (scrollContainerH - targetH) / 2f

                scrollState.animateScrollBy(scrollState.value + delta)
            }
        }
    }
}
