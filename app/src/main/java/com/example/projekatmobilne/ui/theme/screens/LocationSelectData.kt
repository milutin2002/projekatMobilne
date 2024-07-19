package com.example.projekatmobilne.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projekatmobilne.model.LocationData
import com.example.projekatmobilne.viewModels.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSelectScreen(viewModel:LocationViewModel,location:LocationData,onLocationSelected:(LocationData)->Unit){
    val userLocation = remember { mutableStateOf(LatLng(location.latitude, location.longitude)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 20f)
    }

    val markerState = remember { MarkerState(position = userLocation.value) }

    LaunchedEffect(viewModel.location.value) {
        viewModel.location.value?.let {
            val newLatLng = LatLng(it.latitude, it.longitude)
            userLocation.value = newLatLng
            markerState.position = newLatLng
            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 20f)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                userLocation.value = latLng
                markerState.position = latLng
            }
        ) {
            Marker(state = markerState)
        }
        var newLocation: LocationData
        Button(onClick = {
            newLocation = LocationData(userLocation.value.latitude, userLocation.value.longitude)
            onLocationSelected(newLocation)
        }) {
            Text(text = "Set Location")
        }
    }
}