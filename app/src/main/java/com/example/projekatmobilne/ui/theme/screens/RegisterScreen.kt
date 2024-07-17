package com.example.projekatmobilne.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.proj.FirebaseUtil


@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Photo")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        if (photoUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
        Button(onClick = {
            FirebaseUtil.register(username, password, fullName, phoneNumber) {
                navController.navigate("login")
            }
        }) {
            Text("Register")
        }
    }
}