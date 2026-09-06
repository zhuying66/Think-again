package com.zhuying.zaikaolv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zhuying.zaikaolv.AppContainer
import com.zhuying.zaikaolv.data.prefs.ThemeMode
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    private val container: AppContainer,
) : ViewModel() {

    val themeMode: Flow<ThemeMode> = container.settingsDataStore.themeMode

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { MainViewModel(container) }
        }
    }
}
