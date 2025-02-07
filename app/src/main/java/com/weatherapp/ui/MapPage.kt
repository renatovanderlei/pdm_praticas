package com.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.weatherapp.model.MainViewModel
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(viewModel: MainViewModel) {
    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val camPosState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        onMapClick = { location -> viewModel.add(location) },
        onPOIClick = { poi -> viewModel.add(poi.latLng) }
    ) {
        // Add markers for favorite cities with defined locations
        viewModel.cities.forEach { city ->
            if (city.location != null) {
                if (city.weather == null) {
                    viewModel.loadWeather(city)
                }
                Marker(
                    state = MarkerState(position = city.location!!),
                    title = city.name,
                    snippet = city.weather?.desc ?: "Carregando..."
                )
            }
        }
    }
}
