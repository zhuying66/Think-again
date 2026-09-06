package com.zhuying.zaikaolv.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.prefs.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val container: AppContainer,
) : ViewModel() {

    val themeMode: Flow<ThemeMode> = container.settingsDataStore.themeMode

    /** 当前缓存目录大小(字节)。进入设置页时算一次,清除缓存后再算一次。 */
    var cacheBytes by mutableStateOf(0L)
        private set

    init {
        refreshCacheSize()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { container.settingsDataStore.setThemeMode(mode) }
    }

    /**
     * 清空 App 缓存目录。删除在 IO 线程执行,完成后重算一次大小。
     * 注:不要对 cacheDir 加 FileObserver 实时监听——删除/写入产生的事件风暴
     * 会反复触发整目录重算并重组界面,导致设置页卡死。
     */
    fun clearCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                container.appContext.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
            }
            refreshCacheSize()
        }
    }

    /** 删除所有决策记录 */
    fun clearAllRecords() {
        viewModelScope.launch {
            container.database.decisionDao().clear()
        }
    }

    private fun refreshCacheSize() {
        viewModelScope.launch {
            cacheBytes = computeCacheSize(container.appContext)
        }
    }

    private suspend fun computeCacheSize(context: Context): Long =
        withContext(Dispatchers.IO) {
            context.cacheDir.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
        }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(container) }
        }
    }
}
