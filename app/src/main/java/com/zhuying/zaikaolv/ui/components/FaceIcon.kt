package com.zhuying.zaikaolv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 用 Canvas 自绘圆形表情图标(绿色满意 → 红色不满),避免额外图片资源。
 * @param mood 0..3:0 最差(红),3 最好(绿);映射顺序与档位一致。
 */
@Composable
fun FaceIcon(
    mood: Int,
    tint: Color,
    size: Dp = 26.dp,
) {
    val stroke = 2.dp
    Canvas(modifier = Modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        // 外圈
        drawCircle(color = tint, radius = r, center = c, style = Stroke(width = stroke.toPx()))
        // 笑脸弧线角度随 mood 变化
        val faceColor = tint
        val cy = this.size.height * (if (mood >= 2) 0.52f else 0.5f)
        if (mood == 0) {
            // 不开心:嘴角向下
            val start = 45f; val sweep = 90f
            drawArc(
                color = faceColor,
                startAngle = start + 180f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(c.x - r * 0.5f, c.y - r * 0.2f),
                size = androidx.compose.ui.geometry.Size(r, r),
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
            )
        } else {
            val bow = when (mood) { 3 -> 120f; 2 -> 90f; else -> 55f }
            drawArc(
                color = faceColor,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(c.x - r * 0.55f, c.y - r * 0.15f),
                size = androidx.compose.ui.geometry.Size(r * 0.9f, r * 0.9f),
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
            )
            // 眼睛
            val eyeY = c.y - r * 0.22f
            drawCircle(color = faceColor, radius = stroke.toPx() * 0.6f, center = Offset(c.x - r * 0.32f, eyeY))
            drawCircle(color = faceColor, radius = stroke.toPx() * 0.6f, center = Offset(c.x + r * 0.32f, eyeY))
        }
    }
}
