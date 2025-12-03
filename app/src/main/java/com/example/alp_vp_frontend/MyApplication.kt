package com.example.alp_vp_frontend

import android.app.Application
import com.example.alp_vp_frontend.data.container.AppContainer
import com.example.alp_vp_frontend.data.container.DefaultAppContainer

class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}