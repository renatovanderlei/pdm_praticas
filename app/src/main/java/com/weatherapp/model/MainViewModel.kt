package com.weatherapp.model

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.fb.FBDatabase
import com.weatherapp.monitor.ForecastMonitor
import com.weatherapp.ui.nav.Route
import kotlin.random.Random

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService,
    private val monitor: ForecastMonitor
) : ViewModel(), FBDatabase.Listener {

    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) {
            _city.value = tmp?.copy(salt = Random.nextLong())
        }

    init {
        db.setListener(this)
    }

    private fun refresh(city: City) {
        val copy = city.copy(
            salt = Random.nextLong(),
            weather = city.weather?:_cities[city.name]?.weather,
            forecast = city.forecast?:_cities[city.name]?.forecast
        )

    if (_city.value?.name == city.name)  _city.value = copy
    _cities.remove(city.name)
    _cities[city.name] = copy
    }


    fun remove(city: City) {
        db.remove(city)
    }

    fun add(name: String, location: LatLng? = null) {
        db.add(City(name = name, location = location))
    }

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        _cities[city.name] = city
        if (city.isMonitored) {
            monitor.updateCity(city)
        }
    }

    override fun onCityUpdated(city: City) {
        refresh(city)
        if (city.isMonitored) {
            monitor.updateCity(city) // Atualiza worker se monitorado
        } else {
            monitor.cancelCity(city) // Cancela worker se não monitorado
        }
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city.name)
        monitor.cancelCity(city) // Cancela worker e notificações
        if (_city.value?.name == city.name) {
            _city.value = null // Limpa cidade selecionada se for a removida
        }
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name = name, location = LatLng(lat, lng)))
            }
        }
    }

    fun add(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location))
            }
        }
    }

    fun loadWeather(city: City) {
        service.getCurrentWeather(city.name) { apiWeather ->
            city.weather = Weather(
                date = apiWeather?.current?.last_updated ?: "...",
                desc = apiWeather?.current?.condition?.text ?: "...",
                temp = apiWeather?.current?.temp_c ?: -1.0,
                imgUrl = "https:" + apiWeather?.current?.condition?.icon
            )
            refresh(city)
        }
    }

    fun loadForecast(city: City) {
        service.getForecast(city.name) { result ->
            city.forecast = result?.forecast?.forecastday?.map {
                Forecast(
                    date = it.date ?: "00-00-0000",
                    weather = it.day?.condition?.text ?: "Erro carregando!",
                    tempMin = it.day?.mintemp_c ?: -1.0,
                    tempMax = it.day?.maxtemp_c ?: -1.0,
                    imgUrl = ("https:" + it.day?.condition?.icon)
                )
            }
            refresh(city)
        }
    }

    fun loadBitmap(city: City) {
        service.getBitmap(city.weather!!.imgUrl) { bitmap ->
            city.weather!!.bitmap = bitmap
            refresh(city)
        }
    }

    fun update(city: City) {
        db.update(city)
        if (city.isMonitored) {
            monitor.updateCity(city)
        } else {
            monitor.cancelCity(city)
        }
    }

    override fun onUserSigOut() {
        _cities.clear()
        monitor.cancelAll()  // Cancela TODOS os workers e notificações
        _city.value = null   // Limpa a cidade selecionada
    }

    class MainViewModelFactory(
        private val db: FBDatabase,
        private val service: WeatherService,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                val monitor = ForecastMonitor(context)
                return MainViewModel(db, service, monitor) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}