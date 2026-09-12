package com.zhuying.zaikaolv.ui.screens.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.model.VerdictTier
import com.zhuying.zaikaolv.data.room.DecisionRecordEntity
import com.zhuying.zaikaolv.ui.components.AnswerDetailSheet
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen
import com.zhuying.zaikaolv.ui.components.SwipeActionButton
import com.zhuying.zaikaolv.ui.components.SwipeRevealRow
import com.zhuying.zaikaolv.ui.theme.GreenPrimary
import com.zhuying.zaikaolv.ui.theme.Orange
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 左滑露出的操作区宽度(编辑 + 删除两个按钮) */
private val ActionsWidth = 144.dp

@Composable
fun RecordScreen(
    container: AppContainer,
) {
    val vm: RecordViewModel = viewModel(factory = RecordViewModel.factory(container))
    val records by vm.records.collectAsState(initial = emptyList())
    var detail by remember { mutableStateOf<DecisionRecordEntity?>(null) }
    var editing by remember { mutableStateOf<DecisionRecordEntity?>(null) }
    // 当前左滑展开的行(保证同时只有一行展开)
    var revealedId by remember { mutableStateOf<Long?>(null) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                TopBarItem(title = "记录")

                if (records.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("还没有记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = spec.hPad, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(spec.gap),
                    ) {
                        items(records, key = { it.id }) { record ->
                            SwipeRevealRow(
                                revealed = revealedId == record.id,
                                onRevealedChange = { open ->
                                    revealedId = if (open) record.id else null
                                },
                                actionsWidth = ActionsWidth,
                                actionsShape = RoundedCornerShape(16.dp),
                                actions = {
                                    SwipeActionButton(
                                        label = "编辑",
                                        background = MaterialTheme.colorScheme.secondary,
                                        contentColor = Color.White,
                                        onClick = {
                                            revealedId = null
                                            editing = record
                                        },
                                    )
                                    SwipeActionButton(
                                        label = "删除",
                                        background = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White,
                                        onClick = {
                                            revealedId = null
                                            vm.delete(record)
                                        },
                                    )
                                },
                            ) {
                                RecordCard(
                                    record = record,
                                    onClick = {
                                        // 已展开时先收起,否则打开答题详情
                                        if (revealedId == record.id) revealedId = null
                                        else detail = record
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    detail?.let { record ->
        AnswerDetailSheet(
            mode = QuestionMode.fromKey(record.modeKey),
            answersCsv = record.answersCsv,
            onDismiss = { detail = null },
        )
    }

    editing?.let { record ->
        RenameDialog(
            initial = record.title.orEmpty(),
            onDismiss = { editing = null },
            onConfirm = { newName ->
                vm.rename(record, newName)
                editing = null
            },
        )
    }
}

@Composable
private fun TopBarItem(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun RecordCard(record: DecisionRecordEntity, onClick: () -> Unit) {
    val tier = VerdictTier.valueOf(record.tierName)
    val color = when (tier) {
        VerdictTier.RECOMMEND -> GreenPrimary
        VerdictTier.CAUTION -> Orange
        VerdictTier.NOT_RECOMMEND -> Orange
    }
    val mode = QuestionMode.fromKey(record.modeKey)
    val time = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(record.createdAt))
    // 有自定义名称则显示名称,否则回退显示问题大小
    val name = record.title?.takeIf { it.isNotBlank() }
        ?: if (mode == QuestionMode.SMALL) "小问题" else "大问题"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Text(time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tier.label, style = MaterialTheme.typography.titleMedium, color = color)
                Spacer(Modifier.weight(1f))
                Text(
                    "匹配度 ${record.score}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** 居中的名称输入窗口,自动聚焦并拉起系统输入法。 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RenameDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initial) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑名称") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text("输入这条记录的名称") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )

    // 打开后自动聚焦并弹出系统默认输入法
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboard?.show()
    }
}
