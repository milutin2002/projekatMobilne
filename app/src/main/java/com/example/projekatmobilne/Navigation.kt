package com.example.projekatmobilne

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.projekatmobilne.ui.theme.screens.LocationSelectScreen
import com.example.projekatmobilne.ui.theme.screens.RegisterScreen
import com.example.projekatmobilne.ui.theme.screens.shoppingMain
import com.example.projekatmobilne.utils.LocationUtils
import com.example.projekatmobilne.viewModels.LocationViewModel

@Composable
fun Navigation(){
    val navController= rememberNavController()
    val viewModel: LocationViewModel = viewModel()
    val context= LocalContext.current
    val locationUtils= LocationUtils(context)
    NavHost(navController = navController, startDestination = "register"){
        composable("shoppingListScreen"){
            shoppingMain(
                locationUtils = locationUtils,
                viewModel = viewModel,
                navController = navController,
                context = context,
                adress =viewModel.address.value.firstOrNull()?.formatted_address?:"No address"
            )
        }
        composable("register"){
            RegisterScreen(navController = navController)
        }
        dialog("locationScreen"){navBackStackEntry ->
            viewModel.location.value?.let {it1->
                LocationSelectScreen(location = it1) {locationData->
                    viewModel.fetchAddress("${locationData.latitude},${locationData.longitude}")
                    navController.popBackStack()
                }
            }
        }

    }
}