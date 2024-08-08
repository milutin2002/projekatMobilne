package com.example.projekatmobilne.RetrofitPackage

import com.example.projekatmobilne.model.PlaceResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): PlaceResponse
    @GET("place/nearbysearch/json")
    suspend fun getClosestPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): PlaceResponse
}