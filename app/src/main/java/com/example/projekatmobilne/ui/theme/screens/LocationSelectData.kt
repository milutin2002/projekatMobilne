package com.example.projekatmobilne.ui.theme.screens

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.projekatmobilne.Repository.PlaceRepository
import com.example.projekatmobilne.RetrofitPackage.RetrofitInstance
import com.example.projekatmobilne.model.LocationData
import com.example.projekatmobilne.model.Place
import com.example.projekatmobilne.viewModels.LocationViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@SuppressLint("MissingPermission")
@Composable
fun LocationSelectScreen(viewModel: LocationViewModel,location:LocationData,onLocationSelected:(LocationData)->Unit){
    val userLocation = remember { mutableStateOf(LatLng(location.latitude, location.longitude)) }
    val shopLocation = remember { mutableStateOf(LatLng(location.latitude,location.longitude)) }
    val zoom= rememberSaveable{ mutableStateOf(15f) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, zoom.value)
    }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val selectedLocation= remember { mutableStateOf("") }
    val markerState = remember { MarkerState(position = userLocation.value) }
    var nearbyPlaces by remember { mutableStateOf<List<Place>?>(null) }
    var radiusInMeters by rememberSaveable { mutableStateOf(1) }

    suspend fun fetchNearbyPlaces(latLng: LatLng) {
        val placeRepository = PlaceRepository(apiService = RetrofitInstance.apiService)
        val locationString = "${latLng.latitude},${latLng.longitude}"

        nearbyPlaces = placeRepository.fetchClosestPlaces(locationString, radiusInMeters, "shopping_mall", "")
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val newLocation = locationResult.lastLocation
                newLocation?.let {
                    val newLatLng = LatLng(it.latitude, it.longitude)
                    userLocation.value = newLatLng
                    markerState.position = newLatLng

                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        fetchNearbyPlaces(userLocation.value)
    }

    LaunchedEffect(radiusInMeters) {
        fetchNearbyPlaces(userLocation.value)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState)
            nearbyPlaces?.forEach { place ->
                Marker(
                    state = rememberMarkerState(position = LatLng(place.geometry.location.lat, place.geometry.location.lng)),
                    title = place.name, snippet = place.name, onClick = {
                        shopLocation.value=it.position
                        selectedLocation.value=place.name
                        true
                    }
                )
            }

        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Your selected location is ${selectedLocation.value}")
        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Search Radius (meters):")
            BasicTextField(
                value = radiusInMeters.toString(),
                onValueChange = { newValue ->
                    radiusInMeters = newValue.toIntOrNull() ?: 1000
                }
            )
        }

        Button(onClick = {
            val newLocation = LocationData(latitude = shopLocation.value.latitude, longitude = shopLocation.value.longitude)
            onLocationSelected(newLocation)
        }) {
            Text(text = "Set Location")
        }
    }
}
