package com.example.projekatmobilne.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatmobilne.RetrofitPackage.RetrofitClient
import com.example.projekatmobilne.model.GeocodingResponse
import com.example.projekatmobilne.model.GeocodingResults
import com.example.projekatmobilne.model.LocationData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class LocationViewModel:ViewModel() {
    private val _location= mutableStateOf<LocationData?>(null)
    val location : State<LocationData?> = _location
    fun updateLocation(newLocation:LocationData){
        _location.value=newLocation
    }
    private val _address= mutableStateOf(listOf<GeocodingResults>())
    val address:State<List<GeocodingResults>> = _address

    fun fetchAddress(latLng: String){
        try {
            viewModelScope.launch {
                val results=RetrofitClient.create().getAdressFromCordinates(latLng,"")
                _address.value=results.results
            }
        }catch (e:Exception){
            Log.d("res1","${e.cause}")
        }
    }
}