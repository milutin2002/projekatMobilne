package com.example.projekatmobilne.Service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projekatmobilne.MainActivity
import com.example.projekatmobilne.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val latitude = 0.0
        val longitude = 0.0
        showNotification(context, latitude, longitude)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, latitude: Double, longitude: Double) {
        val notification = NotificationCompat.Builder(context, "location_service_channel")
            .setContentTitle("Location Update")
            .setContentText("Lat: $latitude, Lon: $longitude")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(getPendingIntent(context))
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification)
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}