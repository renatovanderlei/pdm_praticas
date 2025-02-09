package com.weatherapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class APIWeatherForecast(
    var location: APILocation? = null,
    var current: APICurrentWeather? = null, // Certifique-se de que esta classe existe
    var forecast: APIForecast? = null
)


interface APIWeatherService {

    companion object {
        const val API_KEY = "ce6eebb5d9084ec9a5124642250702"
    }

    @GET("forecast.json?key=$API_KEY&days=10&lang=pt")
    fun forecast(@Query("q") name: String): Call<APIWeatherForecast?>
}
