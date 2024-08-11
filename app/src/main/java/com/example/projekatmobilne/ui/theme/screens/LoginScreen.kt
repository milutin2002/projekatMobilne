package com.example.projekatmobilne.ui.theme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proj.FirebaseUtil
import com.example.projekatmobilne.viewModels.LocationViewModel

@Composable
fun LoginScreen(navController: NavController,viewModel: LocationViewModel,context:Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(onClick = {
            if(email.isBlank() && password.isBlank()){
                Toast.makeText(context,"Please enter email and password",Toast.LENGTH_LONG).show()
            }
            else {
                FirebaseUtil.signIn(email, password, viewModel, onFailure = {
                    Toast.makeText(context,"Incorrect email or password",Toast.LENGTH_LONG).show()
                }) { s, v ->
                    v.setUserId(s)
                    navController.navigate("userProfile")
                }
            }
        }) {
            Text("Login")
        }
        Button(onClick = {
            navController.navigate("register")
        }) {
            Text("Register")
        }
    }
}