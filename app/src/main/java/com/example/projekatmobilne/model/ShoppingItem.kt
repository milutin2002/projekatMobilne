package com.example.projekatmobilne.model

data class ShoppingItem(
    var id:String="", var name:String="", var quantity:Int=0, var isEditing:Boolean=false, var address:String="", var latitude: Double = 0.0,
    var longitude: Double = 0.0,var userId:String="")
