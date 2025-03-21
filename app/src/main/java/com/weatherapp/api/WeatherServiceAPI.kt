package com.weatherapp.api

import com.weatherapp.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }

    // Procura a localização baseada no nome ou coordenadas
    @GET("search.json?key=$API_KEY&lang=pt_br")
    fun search(@Query("q") query: String): Call<List<APILocation>?>

    // Retorna as condições atuais da cidade
    @GET("current.json?key=$API_KEY&lang=pt")
    fun currentWeather(@Query("q") query: String): Call<APICurrentWeather?>

    // Obtém a previsão do tempo para os próximos 10 dias
    @GET("forecast.json?key=$API_KEY&days=10&lang=pt")
    fun forecast(@Query("q") query: String): Call<APIWeatherForecast?>
}
