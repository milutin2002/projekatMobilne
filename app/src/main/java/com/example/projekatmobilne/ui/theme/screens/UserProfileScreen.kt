package com.example.projekatmobilne.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projekatmobilne.viewModels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: ""
    val userProfileViewModel: UserProfileViewModel = viewModel()
    userProfileViewModel.fetchUserProfile(userId)
    val userProfile by userProfileViewModel.user
    userProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(profile.profileImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
            Text("Full Name: ${profile.name}")
            Text("Email: ${profile.email}")
            Text("Phone: ${profile.phone}")
            Button(
                onClick = {
                    navController.navigate("shoppingListScreen")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Shopping List")
            }
        }
    }
}