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
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.weatherapp.R
import com.weatherapp.model.MainViewModel

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
                val drawable = getDrawable(context, R.drawable.loading)
                val bitmap = drawable?.toBitmap(300, 200)
                var marker = if (bitmap != null)
                    BitmapDescriptorFactory.fromBitmap(bitmap)
                else BitmapDescriptorFactory.defaultMarker()
                if (city.weather == null) {
                    viewModel.loadWeather(city)
                } else if (city.weather!!.bitmap == null) {
                    viewModel.loadBitmap(city)
                } else {
                    marker = BitmapDescriptorFactory
                        .fromBitmap(city.weather!!.bitmap!!.scale(150, 150))
                }

                Marker(
                    state = MarkerState(position = city.location),
                    icon = marker,
                    title = city.name,
                    snippet = city.weather?.desc?:"carregando..."
                )
            }
        }
    }
}
