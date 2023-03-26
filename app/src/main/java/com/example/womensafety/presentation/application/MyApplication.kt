package com.example.womensafety.presentation.application

import android.app.Application
import com.example.womensafety.utils.HelpRepo
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication @Inject constructor(): Application() {
    @Inject
    lateinit var repo: HelpRepo

    override fun onCreate() {
        super.onCreate()
        repo.initSharedPreferences()
    }

}