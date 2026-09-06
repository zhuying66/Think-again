package com.zhuying.zaikaolv.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.prefs.ThemeMode
import com.zhuying.zaikaolv.ui.theme.GreenPrimary
import com.zhuying.zaikaolv.ui.theme.MoonDark
import com.zhuying.zaikaolv.ui.components.ResponsiveScreen

@Composable
fun SettingsScreen(
    container: AppContainer,
) {
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(container))
    val themeMode by vm.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val isSystem = themeMode == ThemeMode.SYSTEM
    var showClearConfirm by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        ResponsiveScreen { spec ->
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                TopBarItem(title = "设置")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spec.hPad),
                ) {
                    SectionTitle("外观设置")
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            // 跟随系统 Switch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spec.vPad),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("跟随系统", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        "跟随系统的深色或浅色模式自动切换",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Switch(
                                    checked = isSystem,
                                    onCheckedChange = { vm.setThemeMode(if (it) ThemeMode.SYSTEM else ThemeMode.LIGHT) },
                                    colors = SwitchDefaults.colors(checkedTrackColor = GreenPrimary),
                                )
                            }

                            Spacer(Modifier.height(10.dp))
                            Text("外观模式", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(spec.gap))

                            ModeRow(
                                icon = { Text("☀", fontSize = 18.sp) },
                                title = "浅色模式",
                                sub = "始终使用浅色主题",
                                selected = themeMode == ThemeMode.LIGHT,
                                enabled = !isSystem,
                                onClick = { vm.setThemeMode(ThemeMode.LIGHT) },
                            )
                            Spacer(Modifier.height(spec.gap))
                            ModeRow(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.DarkMode,
                                        contentDescription = null,
                                        tint = MoonDark,
                                        modifier = Modifier.size(18.dp),
                                    )
                                },
                                title = "深色模式",
                                sub = "始终使用深色主题",
                                selected = themeMode == ThemeMode.DARK,
                                enabled = !isSystem,
                                onClick = { vm.setThemeMode(ThemeMode.DARK) },
                            )
                            Spacer(Modifier.height(spec.vPad))
                        }
                    }

                    Spacer(Modifier.height(spec.gap))
                    // 其他设置
                    SectionTitle("其他设置")
                    // 清除缓存(点击实际清理)
                    CardRow(
                        title = "清除缓存",
                        rightText = formatBytes(vm.cacheBytes),
                        onClick = { vm.clearCache() },
                        spec = spec,
                    )
                    Spacer(Modifier.height(spec.gap))
                    // 删除所有记录
                    CardRow(
                        title = "删除所有记录",
                        subtitle = "清空全部历史记录,不可恢复",
                        rightText = null,
                        onClick = { showClearConfirm = true },
                        spec = spec,
                    )
                    Spacer(Modifier.height(spec.vPad * 2f))
                }
            }
        }
    }

    // 删除所有记录确认弹窗
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearAllRecords()
                    showClearConfirm = false
                }) { Text("确认删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("取消") }
            },
            title = { Text("删除所有记录?") },
            text = { Text("所有历史答题记录将被清空,此操作不可恢复。") },
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
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 14.dp),
    )
}

@Composable
private fun ModeRow(
    icon: @Composable () -> Unit,
    title: String,
    sub: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(sub, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // 单选圆点
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (selected) GreenPrimary else Color.Transparent)
                .then(
                    if (selected) Modifier else Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                )
            }
        }
    }
}

/** 设置页统一风格的卡片行(可点击)。 */
@Composable
private fun CardRow(
    title: String,
    subtitle: String? = null,
    rightText: String? = null,
    onClick: () -> Unit,
    spec: com.zhuying.zaikaolv.ui.components.ResponsiveSpec,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = spec.vPad),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (rightText != null) {
            Text(rightText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** 字节 → 可读大小(B/KB/MB)。 */
private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0B"
    val kb = bytes / 1024.0
    if (kb < 1) return "${bytes}B"
    val mb = kb / 1024.0
    if (mb < 1) return String.format("%.1fKB", kb)
    return String.format("%.1fMB", mb)
}
