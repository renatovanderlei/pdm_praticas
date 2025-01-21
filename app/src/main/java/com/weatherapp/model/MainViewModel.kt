package com.weatherapp.model

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MainViewModel : ViewModel() {
    private val _cities = getCities().toMutableStateList()
    val cities
        get() = _cities.toList()

    fun remove(city: City) {
        _cities.remove(city)
    }

    // Função 'add' com parâmetro opcional 'location'
    fun add(name: String, location: LatLng = null) {
        _cities.add(City(name = name, location = location))
    }
}

// Geração de cidades de exemplo
private fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}
