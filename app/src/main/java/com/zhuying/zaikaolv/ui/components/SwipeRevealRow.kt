package com.zhuying.zaikaolv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * 左滑露出操作按钮的列表行。
 *
 * 手指向左滑动时,前景 [content] 向左平移,露出右侧宽 [actionsWidth] 的 [actions] 区域;
 * 松手后按滑动距离自动吸附到「展开」或「收起」。
 * 轻点(未滑动)不会被吞掉,前景自身的 clickable 仍可正常响应。
 *
 * @param revealed 当前是否展开(由调用方持有,便于保证同时只展开一行)
 * @param onRevealedChange 吸附结果回调
 */
@Composable
fun SwipeRevealRow(
    revealed: Boolean,
    onRevealedChange: (Boolean) -> Unit,
    actionsWidth: Dp,
    actionsShape: Shape = RectangleShape,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val actionsPx = with(LocalDensity.current) { actionsWidth.toPx() }
    var dragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val restOffset = if (revealed) -actionsPx else 0f
    val offset by animateFloatAsState(
        targetValue = if (dragging) dragOffset else restOffset,
        animationSpec = tween(durationMillis = if (dragging) 0 else 200),
        label = "swipeRevealOffset",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clipToBounds()
            .pointerInput(revealed, actionsPx) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragging = true
                        dragOffset = if (revealed) -actionsPx else 0f
                    },
                    onDragEnd = {
                        dragging = false
                        onRevealedChange(dragOffset < -actionsPx / 2f)
                    },
                    onDragCancel = { dragging = false },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset = (dragOffset + dragAmount).coerceIn(-actionsPx, 0f)
                    },
                )
            },
    ) {
        // 垫在右侧的操作区(随滑动逐渐露出)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(actionsWidth)
                .fillMaxHeight()
                .clip(actionsShape),
            content = actions,
        )

        // 前景内容
        Box(modifier = Modifier.offset { IntOffset(offset.roundToInt(), 0) }) {
            content()
        }
    }
}

/** 左滑操作区里的一个按钮(撑满整块高度)。 */
@Composable
fun RowScope.SwipeActionButton(
    label: String,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
