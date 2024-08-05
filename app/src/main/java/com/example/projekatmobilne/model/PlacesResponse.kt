package com.example.projekatmobilne.model

data class PlaceResponse(
    val results: List<Place>,
    val status: String
)

data class Place(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
