package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
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

suspend fun centerVisibleLazyItemByKey(
    lazyListState: LazyListState,
    key: Any,
    fallbackIndex: Int,
) {
    val info = lazyListState.layoutInfo.visibleItemsInfo
        .firstOrNull { it.key == key }

    if (info != null) {
        centerWhenVisible(lazyListState, info)
        return
    }

    val viewportH =
        lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
    lazyListState.animateScrollToItem(fallbackIndex, scrollOffset = -viewportH / 2)

    val newInfo = lazyListState.layoutInfo.visibleItemsInfo
        .firstOrNull { it.key == key } ?: return

    centerWhenVisible(lazyListState, newInfo)
}

private suspend fun centerWhenVisible(
    lazyListState: LazyListState,
    info: LazyListItemInfo,
) {
    val itemCenter = info.offset + info.size / 2

    val viewportStart = lazyListState.layoutInfo.viewportStartOffset
    val viewportEnd = lazyListState.layoutInfo.viewportEndOffset
    val viewportCenter = (viewportStart + viewportEnd) / 2

    val delta = (itemCenter - viewportCenter).toFloat()

    lazyListState.animateScrollBy(delta)
}
