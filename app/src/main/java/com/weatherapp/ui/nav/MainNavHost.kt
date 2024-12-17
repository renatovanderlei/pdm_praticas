package com.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.weatherapp.ui.HomePage
import com.weatherapp.ui.ListPage
import com.weatherapp.ui.MapPage
import com.weatherapp.model.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomePage(viewModel = viewModel) }
        composable("list") { ListPage(viewModel = viewModel) }
        composable("map") { MapPage(viewModel = viewModel) }
    }
}
