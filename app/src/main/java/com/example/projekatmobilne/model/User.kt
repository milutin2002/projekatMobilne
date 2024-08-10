package com.example.projekatmobilne.model

data class User(val profileImageUrl: String, val name: String, val email: String, val phone: String,
                var points:Long=0L){
    constructor() : this("", "", "", "")
}

