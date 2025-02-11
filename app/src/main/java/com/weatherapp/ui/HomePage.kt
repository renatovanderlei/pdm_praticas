package com.weatherapp.ui

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.R
import com.weatherapp.model.Forecast
import com.weatherapp.model.MainViewModel
import androidx.compose.foundation.lazy.items

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if (viewModel.city == null) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(colorResource(id = R.color.teal_700))
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade na lista de favoritas.",
                    fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center, fontSize = 20.sp
                )
            }
            return
        }

        Row {
            Icon(
                imageVector = Icons.Filled.AccountBox,
                contentDescription = "Localized description",
                modifier = Modifier.size(150.dp)
            )
            Column {
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = viewModel.city?.name ?: "Selecione uma cidade...",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = viewModel.city?.weather?.desc ?: "...",
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Temp: " + viewModel.city?.weather?.temp + "℃",
                    fontSize = 22.sp
                )
            }
        }
        if (viewModel.city!!.forecast == null) {
            viewModel.loadForecast(viewModel.city!!); return
        }
        LazyColumn {
            items(viewModel.city!!.forecast!!) { forecast ->
                ForecastItem(forecast, onClick = { })
            }
        }
    }
}


@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick(forecast) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Localização",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = forecast.weather, fontSize = 24.sp)
            Text(text = forecast.date, fontSize = 20.sp)
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = "Min: $tempMin℃", fontSize = 16.sp)
            Text(text = "Max: $tempMax℃", fontSize = 16.sp)
        }
    }
}
