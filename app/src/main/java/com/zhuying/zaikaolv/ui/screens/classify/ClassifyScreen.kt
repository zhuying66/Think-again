package com.zhuying.zaikaolv.ui.screens.classify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen
import com.zhuying.zaikaolv.ui.components.ThinProgressBar
import com.zhuying.zaikaolv.ui.components.TopBar
import com.zhuying.zaikaolv.ui.theme.Blue

/**
 * 「先判断这件事有多重要」——自动问题规模分类的前置步骤。
 *
 * 5 道 1~5 分的标准题(影响 / 时长 / 不可逆 / 代价 / 他人),本地求和后自动归入
 * 小问题或大问题,并进入现有对应的问卷。判定过程完全离线,不涉及 AI / API / 网络。
 */
@Composable
fun ClassifyScreen(
    onExit: () -> Unit,
    onClassified: (QuestionMode) -> Unit,
) {
    val vm: ClassifyViewModel = viewModel()
    var showPicker by remember { mutableStateOf(false) }

    if (vm.showResult) {
        ClassifyResultContent(
            mode = vm.classifiedMode,
            hint = vm.resultHint,
            onStart = { onClassified(vm.classifiedMode) },
            onBack = { vm.goBack() },
            onPickSelf = { showPicker = true },
        )
        if (showPicker) {
            SelfPickDialog(
                onDismiss = { showPicker = false },
                onPick = { mode ->
                    showPicker = false
                    onClassified(mode)
                },
            )
        }
    } else {
        ClassifyQuizContent(
            vm = vm,
            onExit = onExit,
        )
    }
}

// —— 分类问答:5 题,选完自动进入下一题 ——

@Composable
private fun ClassifyQuizContent(
    vm: ClassifyViewModel,
    onExit: () -> Unit,
) {
    val index = vm.currentIndex
    val question = vm.questions[index]

    ResponsiveScreen { spec ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
        ) {
            TopBar(
                title = "",
                rightText = "${index + 1}/${vm.total}",
                onBack = onExit,
            )

            Column(modifier = Modifier.padding(horizontal = spec.hPad)) {
                // 顶部说明:让用户知道这是在判断「问题规模」,而不是直接给结论
                Text(
                    text = "先判断一下这件事有多重要",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "回答几个问题，我们会根据你的回答选择更适合的分析方式",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                ThinProgressBar(fraction = vm.progress)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spec.hPad, vertical = spec.vPad),
            ) {
                Text(
                    text = "第 ${index + 1} / ${vm.total} 题",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.height(spec.gap))

                question.options.forEach { option ->
                    val selected = vm.currentAnswer == option.score
                    ScaleOptionCard(
                        label = option.label,
                        selected = selected,
                        onClick = { vm.select(index, option.score) },
                    )
                    Spacer(Modifier.height(spec.gap))
                }

                Spacer(Modifier.height(16.dp))
            }

            // 底部「上一题 / 下一题」按钮(固定在底部,不随滚动;选中答案后方可进入下一题)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spec.hPad, vertical = spec.vPad),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (index > 0) {
                    OutlinedButton(
                        onClick = { vm.goBack() },
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(spec.dim(56.dp)),
                    ) {
                        Text("上一题", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(spec.gap))
                }
                Button(
                    onClick = { vm.advance(index) },
                    enabled = vm.canGoNext,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.outline,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(spec.dim(56.dp)),
                ) {
                    Text("下一题", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun ScaleOptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else MaterialTheme.colorScheme.surface
    val border = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

// —— 分类结果:只给提示语,不展示分数 ——

@Composable
private fun ClassifyResultContent(
    mode: QuestionMode,
    hint: String,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onPickSelf: () -> Unit,
) {
    val tint = if (mode == QuestionMode.SMALL) Color(0xFF4CAF50) else Blue

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                TopBar(title = "", rightText = "", onBack = onBack)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spec.hPad),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(spec.vPad))

                    Box(
                        modifier = Modifier
                            .size(spec.dim(72.dp))
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (mode == QuestionMode.SMALL) "🪶" else "⛰️",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }

                    Spacer(Modifier.height(spec.gap * 1.2f))
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(spec.gap))
                    Text(
                        text = "接下来进入「${mode.title}」分析",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(spec.gap * 2f))
                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spec.dim(56.dp)),
                    ) {
                        Text("开始分析", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.height(spec.gap))
                    // 允许用户推翻 App 的判断,自己选择规模
                    TextButton(onClick = onPickSelf) {
                        Text(
                            text = "我想自己选择",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(Modifier.height(spec.vPad * 2f))
                }
            }
        }
    }
}

@Composable
private fun SelfPickDialog(
    onDismiss: () -> Unit,
    onPick: (QuestionMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
        title = { Text("选择问题的大小") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PickerRow(
                    emoji = "🪶",
                    title = "小问题",
                    subtitle = "日常小事 · 6 道题快速判断",
                    onClick = { onPick(QuestionMode.SMALL) },
                )
                Spacer(Modifier.height(10.dp))
                PickerRow(
                    emoji = "⛰️",
                    title = "大问题",
                    subtitle = "重要决定 · 16 道题详细分析",
                    onClick = { onPick(QuestionMode.BIG) },
                )
            }
        },
    )
}

@Composable
private fun PickerRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
