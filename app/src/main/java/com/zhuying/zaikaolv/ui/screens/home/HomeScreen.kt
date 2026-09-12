package com.zhuying.zaikaolv.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen
import com.zhuying.zaikaolv.ui.theme.Blue

@Composable
fun HomeScreen(
    onStart: (QuestionMode) -> Unit,
    onStartClassify: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spec.hPad),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(12.dp))
                    // 主标题
                    Text(
                        text = "要不要做这件事?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "通过几个问题,帮你做出更清晰的判断",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(spec.gap * 1.8f))

                    // 主入口:自动判断问题规模(本地问答 + 后台评分,不接入 AI)
                    ClassifyEntryCard(
                        tint = Color(0xFF4CAF50),
                        onClick = onStartClassify,
                    )

                    Spacer(Modifier.height(spec.gap * 1.6f))
                    Text(
                        text = "或者，自己选择问题的大小",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(Modifier.height(spec.gap))
                    QuestionCard(
                        mode = QuestionMode.SMALL,
                        tint = Color(0xFF4CAF50),
                        title = "小问题",
                        subtitle = "日常小事,快速判断",
                        detail = "6 道题",
                        onClick = { onStart(QuestionMode.SMALL) },
                    )
                    Spacer(Modifier.height(spec.gap))
                    QuestionCard(
                        mode = QuestionMode.BIG,
                        tint = Blue,
                        title = "大问题",
                        subtitle = "重要决定,全面分析",
                        detail = "更多题目,更准确",
                        onClick = { onStart(QuestionMode.BIG) },
                    )
                    Spacer(Modifier.height(spec.vPad * 2f))
                }
            }
        }
    }
}

/** 首页主入口:自动判断问题规模。 */
@Composable
private fun ClassifyEntryCard(
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = tint.copy(alpha = 0.12f),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🧭", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "先判断这件事有多重要",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "回答 5 个问题，自动选择适合的分析方式",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun QuestionCard(
    mode: QuestionMode,
    tint: Color,
    title: String,
    subtitle: String,
    detail: String,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 圆形图标:小问题=羽毛🪶,大问题=山⛰️
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (mode == QuestionMode.SMALL) "🪶" else "⛰️",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(detail, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
