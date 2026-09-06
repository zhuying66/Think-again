package com.zhuying.zaikaolv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhuying.zaikaolv.data.prefs.ThemeMode

/** 根据用户主题偏好与当前系统明暗,计算是否用深色。 */
@Composable
fun resolveDarkTheme(mode: ThemeMode): Boolean =
    when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
