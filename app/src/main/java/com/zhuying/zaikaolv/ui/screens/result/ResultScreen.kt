package com.zhuying.zaikaolv.ui.screens.result

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.model.VerdictTier
import com.zhuying.zaikaolv.ui.theme.GreenPrimary
import com.zhuying.zaikaolv.ui.theme.Orange
import com.zhuying.zaikaolv.ui.components.AnswerDetailSheet
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen

@Composable
fun ResultScreen(
    container: AppContainer,
    mode: QuestionMode,
    answersCsv: String,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    val vm: ResultViewModel = viewModel(
        factory = ResultViewModel.factory(mode, answersCsv, container),
    )
    var showDetail by remember { mutableStateOf(false) }
    val result = vm.result
    val tier = result.tier

    val mainColor = when (tier) {
        VerdictTier.RECOMMEND -> GreenPrimary
        VerdictTier.CAUTION -> Orange
        VerdictTier.NOT_RECOMMEND -> Orange
    }
    val title = when (tier) {
        VerdictTier.RECOMMEND -> "推荐去做"
        VerdictTier.CAUTION -> "建议谨慎考虑"
        VerdictTier.NOT_RECOMMEND -> "不太推荐去做"
    }
    val sub = when (tier) {
        VerdictTier.RECOMMEND -> "综合分析结果"
        VerdictTier.CAUTION -> "综合分析结果"
        VerdictTier.NOT_RECOMMEND -> "综合分析结果"
    }
    // 结果说明:按结论 + 用户答案动态生成,不写死整段
    val desc = buildSummary(
        tier = tier,
        highRisk = result.highRisk,
        gateNotMet = result.gateNotMet,
        reasonsDo = result.reasonsDo,
        reasonsDont = result.reasonsDont,
    )
    val points = if (tier == VerdictTier.RECOMMEND) result.reasonsDo else result.reasonsDont
    val pointPrefix = if (tier == VerdictTier.RECOMMEND) "✓" else "⚠"

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                // 顶栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp).clickable { onBack() },
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "结果",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.width(22.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spec.hPad),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(10.dp))
                    // 徽章
                    ResultBadge(tier = tier, color = mainColor, size = spec.dim(84.dp))
                    Spacer(Modifier.height(spec.gap))
                    Text(title, style = MaterialTheme.typography.headlineLarge, color = mainColor)
                    Spacer(Modifier.height(6.dp))
                    Text(sub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(spec.gap))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(spec.gap * 1.4f))
                    // 匹配度卡片
                    MatchCard(pct = result.doLeanPct, color = mainColor)

                    Spacer(Modifier.height(spec.gap))
                    // 主要原因卡片
                    ReasonsCard(title = "主要原因", prefix = pointPrefix, color = mainColor, points = points)

                    Spacer(Modifier.height(spec.gap * 1.6f))
                    // 按钮
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = onRestart,
                            shape = RoundedCornerShape(26.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(spec.dim(52.dp)),
                        ) {
                            Text("重新开始")
                        }
                        Spacer(Modifier.width(14.dp))
                        Button(
                            onClick = { showDetail = true },
                            colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                            shape = RoundedCornerShape(26.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(spec.dim(52.dp)),
                        ) {
                            Text("查看详情")
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    // 免责声明(只显示正文,不显示"免责声明:"字头)
                    Text(
                        text = "本结果仅供参考,不构成任何专业建议,最终决定权在于你自己。",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDetail) {
        AnswerDetailSheet(
            mode = mode,
            answersCsv = answersCsv,
            onDismiss = { showDetail = false },
        )
    }
}

@Composable
private fun ResultBadge(tier: VerdictTier, color: Color, size: androidx.compose.ui.unit.Dp = 84.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.52f)) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val s = this.size.minDimension / 2f
            if (tier == VerdictTier.RECOMMEND) {
                // 勾
                val p = Path()
                p.moveTo(c.x - s * 0.45f, c.y)
                p.lineTo(c.x - s * 0.1f, c.y + s * 0.4f)
                p.lineTo(c.x + s * 0.55f, c.y - s * 0.45f)
                drawPath(p, Color.White, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
            } else {
                // 叹号
                drawLine(Color.White, Offset(c.x, c.y - s * 0.5f), Offset(c.x, c.y + s * 0.15f), strokeWidth = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawCircle(Color.White, radius = 5f, center = Offset(c.x, c.y + s * 0.5f))
            }
        }
    }
}

@Composable
private fun MatchCard(pct: Int, color: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("匹配度", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("$pct%", style = MaterialTheme.typography.headlineLarge, color = color)
            Spacer(Modifier.height(10.dp))
            // 进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pct / 100f)
                        .height(10.dp)
                        .background(color, RoundedCornerShape(6.dp)),
                )
            }
        }
    }
}

@Composable
private fun ReasonsCard(title: String, prefix: String, color: Color, points: List<String>) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            if (points.isEmpty()) {
                Text("——", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                points.forEach { pt ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(prefix, color = color, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(pt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * 结果说明:根据结论 + 用户答案动态生成。
 * 主体按三档给文案,触发高风险项 / 条件不足时追加提示,并把最主要的收益/顾虑维度融进说明。
 */
private fun buildSummary(
    tier: VerdictTier,
    highRisk: Boolean,
    gateNotMet: Boolean,
    reasonsDo: List<String>,
    reasonsDont: List<String>,
): String {
    val base = when (tier) {
        VerdictTier.RECOMMEND -> "综合来看，这件事的潜在收益和意义大于可能产生的成本与风险。"
        VerdictTier.CAUTION -> "这件事存在一定价值，但目前仍有一些因素需要进一步权衡。"
        VerdictTier.NOT_RECOMMEND -> "综合来看，这件事的潜在成本或风险可能超过它能带来的收益。"
    }
    val extra = buildString {
        if (gateNotMet) {
            append("你目前的承担能力或基本条件可能还不足，建议先补齐再考虑。")
        }
        if (highRisk) {
            append("其中一项涉及较高风险，请重点权衡。")
        }
        val top = if (tier == VerdictTier.RECOMMEND) reasonsDo.firstOrNull() else reasonsDont.firstOrNull()
        if (tier != VerdictTier.CAUTION && top != null) {
            append("主要基于：").append(top).append("。")
        }
    }
    return if (extra.isEmpty()) base else base + extra
}
