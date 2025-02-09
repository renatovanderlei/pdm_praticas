package com.weatherapp.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    private var weatherAPI: WeatherServiceAPI
    init {
        //Crio a instância retrofit e implemento a interface WeatherService
        val retrofitAPI = Retrofit.Builder().baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }
    //Recebe latitude e longitude e retorna o nome da localização.
    fun getName(lat: Double, lng: Double, onResponse : (String?) -> Unit ) {
        search("$lat,$lng") { loc -> onResponse (loc?.name) }
    }
    //Recebe um nome de localização e retorna a latitude e longitude.
    fun getLocation(name: String, onResponse: (lat:Double?, long:Double?) -> Unit) {
        search(name) { loc -> onResponse (loc?.lat, loc?.lon) }
    }

    //Faz a busca com base na query.
    private fun search(query: String, onResponse : (APILocation?) -> Unit) {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        call.enqueue(object : Callback<List<APILocation>?> {
            override fun onResponse(call: Call<List<APILocation>?>,
                                    response: Response<List<APILocation>?>
            ) {
                onResponse(response.body()?.let {if (it.isNotEmpty()) it[0] else null})
            }
            override fun onFailure(call: Call<List<APILocation>?>,t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
                onResponse(null)
            }
        })
    }
    //faz o enfileiramento das requisições do Retrofit, evitando a repetição de código
    private fun <T> enqueue(call : Call<T?>, onResponse : ((T?) -> Unit)? = null){
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                val obj: T? = response.body()
                onResponse?.invoke(obj)
            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
            }
        })
    }
    //aciona o nomo endpoint para buscar as condições climáticas
    //atuais da cidade, usando a nossa função genérica do Retrofit.
    fun getCurrentWeather(name: String, onResponse: (APICurrentWeather?) -> Unit){
        val call: Call<APICurrentWeather?> = weatherAPI.currentWeather(name)
        enqueue(call) { onResponse.invoke(it) }
    }

    fun getForecast(name: String, onResponse : (APIWeatherForecast?) -> Unit) {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        enqueue(call) { onResponse.invoke(it) }
    }
}
