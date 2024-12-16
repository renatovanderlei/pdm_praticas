package com.weatherapp.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector,
    var route: String
) {
    object HomeButton : BottomNavItem("Início", Icons.Default.Home, "home")
    object ListButton : BottomNavItem("Favoritos", Icons.Default.Favorite, "list")
    object MapButton : BottomNavItem("Mapa", Icons.Default.LocationOn, "map")
}
