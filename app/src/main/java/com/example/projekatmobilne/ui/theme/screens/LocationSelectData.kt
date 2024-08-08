package com.example.projekatmobilne.ui.theme.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projekatmobilne.Repository.PlaceRepository
import com.example.projekatmobilne.RetrofitPackage.RetrofitInstance
import com.example.projekatmobilne.model.LocationData
import com.example.projekatmobilne.model.Place
import com.example.projekatmobilne.viewModels.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun LocationSelectScreen(viewModel:LocationViewModel,location:LocationData,onLocationSelected:(LocationData)->Unit){
    val userLocation = remember { mutableStateOf(LatLng(location.latitude, location.longitude)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 15f)
    }

    val markerState = remember { MarkerState(position = userLocation.value) }
    val markerState1 = remember { MarkerState(position = userLocation.value) }

    var nearbyPlaces by remember { mutableStateOf<List<Place>?>(null) }
    var radiusInMeters by remember { mutableStateOf(1) }

    suspend fun fetchNearbyPlaces(latLng: LatLng) {
        val placeRepository = PlaceRepository(apiService = RetrofitInstance.apiService)
        val locationString = "${latLng.latitude},${latLng.longitude}"

        nearbyPlaces = placeRepository.fetchClosestPlaces(locationString, radiusInMeters, "shopping_mall", "")
    }

    LaunchedEffect(Unit) {
        // Initial fetch of nearby places
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
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                userLocation.value = latLng
                markerState1.position = latLng
                // Fetch nearby places for the new location
            }
        ) {
            Marker(state = markerState)
            Marker(state = markerState1)
            // Display markers for all nearby places
            nearbyPlaces?.forEach { place ->
                Marker(
                    state = rememberMarkerState(position = LatLng(place.geometry.location.lat, place.geometry.location.lng)),
                    title = place.name, snippet = place.name
                )
            }

        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Search Radius (meters):")
            BasicTextField(
                value = radiusInMeters.toString(),
                onValueChange = { newValue ->
                    radiusInMeters = newValue.toIntOrNull() ?: 1000 // Fallback to 1000 meters if input is invalid
                }
            )
        }

        Button(onClick = {
            val newLocation = LocationData(markerState1.position.latitude, markerState1.position.longitude)
            onLocationSelected(newLocation)
        }) {
            Text(text = "Set Location")
        }
    }
}
