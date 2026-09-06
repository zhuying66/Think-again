package com.zhuying.zaikaolv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 根据页面可用宽度生成的响应式规格。手机长宽比范围内(约 320~500dp 宽)自适应,
 * 超出 480dp 时内容限宽居中,避免大屏被拉得过宽。
 */
data class ResponsiveSpec(
    val hPad: Dp,      // 水平内边距
    val vPad: Dp,      // 垂直内边距
    val gap: Dp,       // 卡片/按钮/间距
    val scale: Float,  // 尺寸缩放因子(用于插画/徽章/按钮高)
) {
    /** 基于基准值的缩放尺寸 */
    fun dim(base: Dp): Dp = base * scale
}

private val MAX_CONTENT_WIDTH = 480.dp

/**
 * 响应式页面容器:
 * - 用 BoxWithConstraints 读取可用宽高,计算 ResponsiveSpec
 * - 内容包在 widthIn(max=480dp) 里并 top-center 对齐,实现大屏限宽居中
 */
@Composable
fun ResponsiveScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(ResponsiveSpec) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        val spec = ResponsiveSpec(
            hPad = when {
                w < 360.dp -> 16.dp
                w < 430.dp -> 24.dp
                else -> 32.dp
            },
            vPad = when {
                h < 720.dp -> 16.dp
                else -> 24.dp
            },
            gap = when {
                w < 360.dp -> 12.dp
                w < 430.dp -> 14.dp
                else -> 16.dp
            },
            scale = when {
                w < 360.dp -> 0.88f
                w < 430.dp -> 1f
                else -> 1.08f
            },
        )

        // 限宽居中
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = MAX_CONTENT_WIDTH)
                .align(Alignment.TopCenter),
        ) {
            content(spec)
        }
    }
}
