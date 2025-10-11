package com.educacionit.biciya

import android.app.Application
import com.educacionit.biciya.utils.notification.NotificationHelper

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
