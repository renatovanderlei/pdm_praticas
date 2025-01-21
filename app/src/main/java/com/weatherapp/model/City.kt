package com.weatherapp.model

import com.google.android.gms.maps.model.LatLng

data class City(
    val name: String,
    val weather: String = "Carregando clima...",
    val location: LatLng? = null // Suporte para localização
)
