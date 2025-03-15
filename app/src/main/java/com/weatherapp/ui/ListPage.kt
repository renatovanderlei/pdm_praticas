package com.weatherapp.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.model.City
import com.weatherapp.model.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.weatherapp.ui.nav.Route
import com.weatherapp.R


@Composable
fun ListPage(
    viewModel: MainViewModel, modifier: Modifier = Modifier,
) {
    val cityList = viewModel.cities
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cityList) { city ->
            if (city.weather == null) {
                viewModel.loadWeather(city)
            }

            CityItem(city = city, onClick = {
                viewModel.city = city
                viewModel.page = Route.Home
            }, onClose = {
                viewModel.remove(city)
                Toast.makeText(context, "${city.name} removido", Toast.LENGTH_SHORT).show()
            })
        }
    }
}

@Composable
fun CityItem(
    city: City,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        // Ícone de notificação (adicionado acima do nome da cidade)
        Icon(
            imageVector = if (city.isMonitored) Icons.Filled.Notifications else Icons.Outlined.Notifications,
            contentDescription = "Monitorada?",
            modifier = Modifier
                .size(32.dp) // Tamanho ajustado
                .align(Alignment.CenterHorizontally) // Centralizado horizontalmente
        )

        Spacer(modifier = Modifier.size(8.dp)) // Espaçamento entre o ícone e o texto

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = modifier.weight(1f)) {
                Text(
                    text = city.name,
                    fontSize = 24.sp
                )
                Text(
                    text = city.weather?.desc ?: "Carregando clima...",
                    fontSize = 16.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Fechar")
            }
        }
    }
}