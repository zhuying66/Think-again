package com.zhuying.zaikaolv.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen
import com.zhuying.zaikaolv.ui.components.ThinProgressBar
import com.zhuying.zaikaolv.ui.components.TopBar

// 2 档答案:是 / 不是。scoreIdx 1=是(认同),0=不是(不认同)
private data class AnswerOption(val scoreIdx: Int, val label: String)

private fun answerOptions(): List<AnswerOption> = listOf(
    AnswerOption(1, "是"),
    AnswerOption(0, "不是"),
)

@Composable
fun QuizScreen(
    mode: QuestionMode,
    onBack: () -> Unit,
    onFinished: (QuestionMode, List<Int>) -> Unit,
) {
    val vm: QuizViewModel = viewModel(factory = QuizViewModel.factory(mode))
    val options = answerOptions()
    val currentIndex = vm.currentIndex

    LaunchedEffect(vm.allAnswered) {
        if (vm.allAnswered) {
            onFinished(mode, vm.answers.filterNotNull())
        }
    }

    ResponsiveScreen { spec ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
        ) {
            TopBar(
                title = if (mode == QuestionMode.SMALL) "小问题" else "大问题",
                rightText = "${vm.currentIndex + 1}/${vm.total}",
                onBack = { onBack() },
            )

            Column(modifier = Modifier.padding(horizontal = spec.hPad)) {
                ThinProgressBar(fraction = vm.progress)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spec.hPad, vertical = spec.vPad),
            ) {
                val q = vm.questions[currentIndex]
                Text(
                    text = q.text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "考虑这件事对你情绪和心理状态的影响",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(spec.gap))

                options.forEach { option ->
                    val selected = vm.answers[currentIndex] == option.scoreIdx
                    AnswerCard(
                        label = option.label,
                        selected = selected,
                        onClick = { vm.select(currentIndex, option.scoreIdx) },
                    )
                    Spacer(Modifier.height(spec.gap))
                }

                Spacer(Modifier.height(16.dp))
            }

            // 底部「上一题 / 下一题」按钮(固定在底部,不随滚动)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spec.hPad, vertical = spec.vPad),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (currentIndex > 0) {
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
                    onClick = { if (vm.canGoNext) vm.selectNext() },
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
private fun AnswerCard(
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
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
