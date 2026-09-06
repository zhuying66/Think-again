package com.zhuying.zaikaolv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zhuying.zaikaolv.ui.MainViewModel
import com.zhuying.zaikaolv.ui.navigation.ZaikaolvRoot
import com.zhuying.zaikaolv.ui.theme.ZaikaolvTheme

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels {
        MainViewModel.factory((application as ZaikaolvApplication).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ZaikaolvApp()
        }
    }

    @Composable
    private fun ZaikaolvApp() {
        val themeMode by vm.themeMode.collectAsState(initial = com.zhuying.zaikaolv.data.prefs.ThemeMode.SYSTEM)
        val dark = com.zhuying.zaikaolv.ui.theme.resolveDarkTheme(themeMode)
        ZaikaolvTheme(darkTheme = dark) {
            ZaikaolvRoot(container = (application as ZaikaolvApplication).container)
        }
    }
}
