package com.example.projekatmobilne

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projekatmobilne.model.LocationData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun locationSelectScreen(location:LocationData,onLocationSelected:(LocationData)->Unit){
    val userLocation= remember {
        mutableStateOf(LatLng(location.latitude,location.longitude))
    }
    val cameraPositionState= rememberCameraPositionState{
        position= CameraPosition.fromLatLngZoom(userLocation.value,10f)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(modifier = Modifier
            .weight(1f)
            .padding(top = 16.dp), cameraPositionState = cameraPositionState, onMapClick = {
                userLocation.value=it
        }){
            Marker(state = MarkerState(position = userLocation.value))
        }
        var newLocation:LocationData
        Button(onClick = {
            newLocation=LocationData(userLocation.value.latitude,userLocation.value.longitude)
        }) {
            Text(text = "Set Location")
        }

    }
}