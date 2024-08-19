package com.example.projekatmobilne.ui.theme.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proj.FirebaseUtil.addItem
import com.example.proj.FirebaseUtil.fetchShoppingItems
import com.example.proj.FirebaseUtil.updateUserPoints
import com.example.projekatmobilne.utils.LocationUtils
import com.example.projekatmobilne.MainActivity
import com.example.projekatmobilne.Service.NotificationWorker
import com.example.projekatmobilne.model.ShoppingItem
import com.example.projekatmobilne.viewModels.LocationViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun shoppingMain(locationUtils: LocationUtils, viewModel: LocationViewModel, navController: NavController, context: Context, adress:String,latitude: Double, longitude: Double) {
    var shopItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var filteredItems by remember {mutableStateOf(listOf<ShoppingItem>())}
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("0") }
    var searchField by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true)) {
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            } else {
                val rationaleRequired = (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )) && (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ))

                if (rationaleRequired) {
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        fetchShoppingItems(userId = viewModel.id.value,
            onSuccess = { fetchedItems ->
             shopItems=fetchedItems
                filteredItems=fetchedItems
            },
            onFailure = { exception ->
                var errorMessage = "Error fetching items: ${exception.message}"
            }
        )
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add item")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                } else {
                    requestPermissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
                }
                setupPeriodicWork(context)
            }) {
                Text(text = "Start Location Service")
            }

            Button(onClick = {
                stopPeriodicWork(context)
            }) {
                Text(text = "Stop Location Service")
            }
        }
        OutlinedTextField(
            value = searchField,
            onValueChange = { searchField = it
                            if(searchField!=""){
                                filteredItems=shopItems.filter {
                                    item->
                                    item.name.contains(searchField)
                                }
                            }
                            else{
                                filteredItems=shopItems
                            }
                            },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(filteredItems) {
                    item->
                    ShoppingItemShow(item = item)
            }
        }

    }
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, confirmButton = {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
                Button(onClick = {
                    if(itemName.isNotBlank()){
                        val newItem= ShoppingItem(name = itemName, quantity = itemQuantity.toInt(),id="", address = adress, latitude = latitude, longitude = longitude)
                        addItem(newItem,viewModel, onSuccess = {
                            item->
                            shopItems=shopItems+item
                            filteredItems=filteredItems+item
                            Toast.makeText(context,"Success in adding item",Toast.LENGTH_LONG).show()
                        }){ e->
                            Log.e("Error in adding",e.message.toString())
                            Toast.makeText(context,"Failure in adding item",Toast.LENGTH_LONG).show()
                        }
                        itemName="";
                        itemQuantity="0"
                        showDialog=false
                    }
                }) {
                    Text(text = "Add")
                }
            }
        }, title = { Text(text = "Shopping list item") }, text = {
            Column {
                OutlinedTextField(
                    value = itemName, onValueChange = {
                        itemName = it
                    }, singleLine = true, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                OutlinedTextField(
                    value = itemQuantity, onValueChange = {
                        if(it.isDigitsOnly()) {
                            itemQuantity = it
                        }
                    }, singleLine = true, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Button(onClick = {
                    if(locationUtils.hasLocationPermission(context)){
                        locationUtils.requestLocationUpdates(viewModel)
                        viewModel.shoppingItems.value=filteredItems
                        navController.navigate("locationscreen"){
                            this.launchSingleTop
                        }
                    }
                    else{
                        requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION))
                    }
                }) {
                    Text(text = "Adress")
                }
            }
        })
    }
}
@Composable
fun ShoppingItemShow(
    item: ShoppingItem
){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement =  Arrangement.SpaceBetween
    ){
        Column(modifier = Modifier
            .weight(1f)
            .padding(8.dp)) {
            Row{
                Text(text = item.name, modifier = Modifier.padding(8.dp))
                Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
            }
            Row(modifier= Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = item.address)
            }
        }

    }
}

fun setupPeriodicWork(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "LocationNotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
}
fun stopPeriodicWork(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("LocationNotificationWork")
}
