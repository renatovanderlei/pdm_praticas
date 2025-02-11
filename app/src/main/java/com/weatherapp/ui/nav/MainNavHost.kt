package com.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.weatherapp.model.MainViewModel
import com.weatherapp.ui.HomePage
import com.weatherapp.ui.ListPage
import com.weatherapp.ui.MapPage

@Composable
fun MainNavHost(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel)  }
        composable<Route.List> { ListPage(viewModel)  }
        composable<Route.Map>  { MapPage(viewModel)   }
    }
}