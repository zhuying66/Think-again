package com.zhuying.zaikaolv.ui.screens.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.model.QuestionMode
import com.zhuying.zaikaolv.data.model.VerdictTier
import com.zhuying.zaikaolv.data.room.DecisionRecordEntity
import com.zhuying.zaikaolv.ui.theme.Blue
import com.zhuying.zaikaolv.ui.theme.GreenPrimary
import com.zhuying.zaikaolv.ui.theme.Orange
import com.zhuying.zaikaolv.ui.components.AnswerDetailSheet
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordScreen(
    container: AppContainer,
) {
    val vm: RecordViewModel = viewModel(factory = RecordViewModel.factory(container))
    val records by vm.records.collectAsState(initial = emptyList())
    var detail by remember { mutableStateOf<DecisionRecordEntity?>(null) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                TopBarItem(title = "记录")

                if (records.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("还没有记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = spec.hPad, vertical = 8.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spec.gap),
                    ) {
                        items(records) { record ->
                            RecordCard(record, onClick = { detail = record })
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
                    text = if (mode == QuestionMode.SMALL) "小问题" else "大问题",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tier.label, style = MaterialTheme.typography.titleMedium, color = color)
                Spacer(Modifier.weight(1f))
                Text("匹配度 ${record.score}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
