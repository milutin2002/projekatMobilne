package com.example.projekatmobilne

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.projekatmobilne.model.ShoppingItem
import com.example.projekatmobilne.ui.theme.ProjekatMobilneTheme
import com.example.projekatmobilne.viewModels.LocationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjekatMobilneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun shoppingMain(locationUtils: LocationUtils, viewModel: LocationViewModel, navController:NavController, context: Context, adress:String) {
    var shopItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("0") }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult = { permissions ->
            if(permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // I HAVE ACCESS to location

                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }else{
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                        .show()
                }else{
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shopItems) {
                item->
                if(item.isEditing){
                    ShoppingItemEditor(shopingItem = item) {
                        editedName,editQuantity->
                            shopItems = shopItems.map { it.copy(isEditing = false) }
                            val editedItem = shopItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editQuantity
                                it.address=adress
                            }
                    }
                }
                else{
                    ShoppingItemShow(item = item, onEditClick = {
                        shopItems=shopItems.map { it.copy(isEditing = it.id==item.id) }
                    }, onDeleteClick = {
                        shopItems=shopItems-item
                    })
                }

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
                        val newItem=ShoppingItem(name = itemName, quantity = itemQuantity.toInt(),id=shopItems.size+1, address = adress)
                        shopItems=shopItems+newItem
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
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            Row{
                Text(text = item.name, modifier = Modifier.padding(8.dp))
                Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
            }
            Row(modifier= Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = item.address)
            }
        }

        Row(modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick){
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }

            IconButton(onClick = onDeleteClick){
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

        }
    }
}

@Composable
fun ShoppingItemEditor(shopingItem:ShoppingItem,onEditComplete:(String,Int)->Unit){
    var editName by remember { mutableStateOf(shopingItem.name) }
    var editQuantity by remember { mutableStateOf(shopingItem.quantity.toString()) }
    var isEditing by remember { mutableStateOf(shopingItem.isEditing) }
    Row (modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly){
        Column {
            BasicTextField(value = editName, onValueChange ={
                editName=it
            } , singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
            BasicTextField(value = editQuantity, onValueChange ={
                if(it.isDigitsOnly()){
                    editQuantity=it
                }
            } , singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
            Button(onClick = {
                isEditing=false
                onEditComplete(editName,editQuantity.toInt())
            }) {
                Text(text = "Save")
            }
        }
    }
}
@Composable
fun Navigation(){
    val navController=rememberNavController()
    val viewModel:LocationViewModel= viewModel()
    val context= LocalContext.current
    val locationUtils=LocationUtils(context)
    NavHost(navController = navController, startDestination = "shoppingListScreen"){
        composable("shoppingListScreen"){
            shoppingMain(
                locationUtils = locationUtils,
                viewModel = viewModel,
                navController = navController,
                context = context,
                adress =viewModel.address.value.firstOrNull()?.formatted_address?:"No address"
            )
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
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
