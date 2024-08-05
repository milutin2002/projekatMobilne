package com.example.projekatmobilne.Repository

import android.util.Log
import com.example.projekatmobilne.RetrofitPackage.PlacesApiService
import com.example.projekatmobilne.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceRepository(private val apiService: PlacesApiService) {

    suspend fun fetchClosestPlace(location: String, radius: Int, type: String, apiKey: String): Place? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNearbyPlaces(location, radius, type, apiKey)
                Log.d("API_RESPONSE", "Response: ${response.toString()}")

                if (response.results.isNotEmpty()) {
                    // Return the closest place (first result)
                    response.results[0]
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.d("Exception_api", e.message.toString())
                null
            }
        }
    }
}


