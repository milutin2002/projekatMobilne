package com.example.projekatmobilne.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.projekatmobilne.MainActivity
import com.example.projekatmobilne.R
import com.example.projekatmobilne.Repository.PlaceRepository
import com.example.projekatmobilne.RetrofitPackage.RetrofitInstance
import com.example.projekatmobilne.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import com.google.android.gms.maps.model.LatLng

class NotificationWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val locationUtils = LocationUtils(applicationContext)
        if(locationUtils.hasLocationPermission(context = applicationContext) && locationUtils.hasBackgroundLocationPermission()) {
            val location = withContext(Dispatchers.IO) {
                getLocation(locationUtils)
            }

            if (location != null) {
                val placesRepository = PlaceRepository(RetrofitInstance.apiService)
                val apiKey = ""
                val places = withContext(Dispatchers.IO) {
                    placesRepository.fetchClosestPlace(
                        "${location.latitude},${location.longitude}",
                        1000,
                        "shopping_mall",
                        apiKey
                    )
                }
                Log.d("Place ", places.toString())
                if (places != null) {
                    sendNotification("Nearest place: ${places.name}")
                } else {
                    sendNotification("No nearby stores found")
                }
            } else {
                sendNotification("Unable to get location")
            }
        }
        return Result.success()
    }

    private suspend fun getLocation(locationUtils: LocationUtils): LatLng? {
        return suspendCancellableCoroutine { continuation ->
            locationUtils.getCoordinates { location ->
                continuation.resume(location) {}
            }
        }
    }

    private fun sendNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Nearby Store")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
}