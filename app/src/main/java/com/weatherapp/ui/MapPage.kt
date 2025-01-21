package com.weatherapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.weatherapp.model.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val cities = viewModel.cities

    // Estado da posição da câmera
    val camPosState = rememberCameraPositionState()

    // Declaração das localizações
    val recife = LatLng(-8.05, -34.9)
    val caruaru = LatLng(-8.27, -35.98)
    val joaopessoa = LatLng(-7.12, -34.84)

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        onMapClick = { viewModel.add("Nova cidade", location = it) }
    ) {
        // Adicionar marcadores para as localizações predefinidas
        Marker(
            state = MarkerState(position = recife),
            title = "Recife",
            snippet = "Marcador em Recife",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
        Marker(
            state = MarkerState(position = caruaru),
            title = "Caruaru",
            snippet = "Marcador em Caruaru",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )
        Marker(
            state = MarkerState(position = joaopessoa),
            title = "João Pessoa",
            snippet = "Marcador em João Pessoa",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )

        // Adicionar marcadores para as cidades favoritas com localização definida
        viewModel.cities.forEach {
            if (it.location != null) {
                Marker(
                    state = MarkerState(position = it.location),
                    title = it.name,
                    snippet = "${it.location}"
                )
            }
        }
    }
}
