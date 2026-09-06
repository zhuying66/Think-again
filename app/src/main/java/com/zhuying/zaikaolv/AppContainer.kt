package com.zhuying.zaikaolv

import android.content.Context
import com.zhuying.zaikaolv.data.prefs.SettingsDataStore
import com.zhuying.zaikaolv.data.room.AppDatabase
import com.zhuying.zaikaolv.domain.DecisionEngine
import com.zhuying.zaikaolv.domain.LocalRuleEngine

/** 简单手工 DI:集中提供单例,便于替换实现(如 AI 引擎)。 */
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext
    val settingsDataStore: SettingsDataStore = SettingsDataStore(appContext)
    val database: AppDatabase = AppDatabase.get(appContext)
    val decisionEngine: DecisionEngine = LocalRuleEngine()
}
