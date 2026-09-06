package com.zhuying.zaikaolv

import android.app.Application

class ZaikaolvApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
