package com.weatherapp.fb

import com.google.android.gms.maps.model.LatLng
import com.weatherapp.model.City
import com.weatherapp.model.Weather

class FBCity {
    var name: String? = null
    var lat: Double? = null
    var lng: Double? = null
    var monitored: Boolean = false

    fun toCity(): City {
        val latlng = if (lat != null && lng != null) LatLng(lat!!, lng!!) else null
        return City(
            name = name!!,
            isMonitored = monitored, // Mapeando o atributo
            location = latlng,
            salt = null, // Pode ser ajustado conforme necessário
            weather = null,
            forecast = null
        )
    }
}

fun City.toFBCity(): FBCity {
    val fbCity = FBCity()

    fbCity.name = this.name
    fbCity.lat = this.location?.latitude
    fbCity.lng = this.location?.longitude
    fbCity.monitored = this.isMonitored
    return fbCity
}
