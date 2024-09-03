package com.example.projekatmobilne

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.projekatmobilne.ui.theme.screens.LeaderboardScreen
import com.example.projekatmobilne.ui.theme.screens.LocationSelectScreen
import com.example.projekatmobilne.ui.theme.screens.LoginScreen
import com.example.projekatmobilne.ui.theme.screens.RegisterScreen
import com.example.projekatmobilne.ui.theme.screens.UserProfileScreen
import com.example.projekatmobilne.ui.theme.screens.shoppingMain
import com.example.projekatmobilne.utils.LocationUtils
import com.example.projekatmobilne.viewModels.LocationViewModel
import com.example.projekatmobilne.viewModels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Navigation(){
    val navController= rememberNavController()
    val viewModel: LocationViewModel = viewModel()
    val context= LocalContext.current
    val locationUtils= LocationUtils(context)
    locationUtils.requestLocationUpdates(viewModel)
    val userViewModel:UserProfileViewModel=viewModel()
    var startDestination:String="login"
    FirebaseAuth.getInstance().currentUser?.let {
        startDestination="userProfile"
        viewModel.setUserId(it.uid)
    }
    NavHost(navController = navController, startDestination = startDestination){
        composable("shoppingListScreen"){
            viewModel.location.value?.let { it1 ->
                shoppingMain(
                    locationUtils = locationUtils,
                    viewModel = viewModel,
                    navController = navController,
                    context = context,
                    adress =viewModel.address.value.firstOrNull()?.formatted_address?:"No address",
                    longitude = it1.longitude,
                    latitude = it1.longitude
                )
            }
        }
        composable("login"){
            LoginScreen(navController = navController,viewModel,context)
        }
        composable("register"){
            RegisterScreen(navController = navController,userViewModel,context)
        }
        composable("userProfile"){
            UserProfileScreen(navController = navController)
        }
        composable("leaderboard"){
            LeaderboardScreen()
        }
        dialog("locationScreen"){navBackStackEntry ->
            viewModel.location.value?.let {it1->
                LocationSelectScreen(viewModel,location = it1) {locationData->
                    viewModel.updateLocation(locationData)
                    viewModel.fetchAddress("${locationData.latitude},${locationData.longitude}")
                    navController.popBackStack()
                }
            }
        }

    }
}