package com.example.projekatmobilne.Service

import com.example.projekatmobilne.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("maps/api/geocode/json")
    suspend fun getAdressFromCordinates(@Query("latlng")latlng:String,@Query("key")apiKey:String):GeocodingResponse
}