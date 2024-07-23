package com.example.projekatmobilne.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.projekatmobilne.Service.NotificationWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context!!)
                .enqueueUniquePeriodicWork(
                    "LocationNotificationWork",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }
}