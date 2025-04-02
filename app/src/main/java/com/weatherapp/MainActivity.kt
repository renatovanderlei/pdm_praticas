package com.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weatherapp.api.WeatherService
import com.weatherapp.fb.FBDatabase
import com.weatherapp.model.MainViewModel
import com.weatherapp.ui.CityDialog
import com.weatherapp.ui.nav.BottomNavBar
import com.weatherapp.ui.nav.BottomNavItem
import com.weatherapp.ui.nav.MainNavHost
import com.weatherapp.ui.nav.Route
import com.weatherapp.ui.theme.WeatherAppTheme
import androidx.navigation.NavDestination.Companion.hasRoute
import com.weatherapp.monitor.ForecastMonitor

//class MainActivity : ComponentActivity() {
//    @OptIn(ExperimentalMaterial3Api::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val fbDB = remember { FBDatabase() }
//            val weatherService = remember { WeatherService() }
//            val forecastMonitor = remember { ForecastMonitor(this@MainActivity) }
//            val viewModel: MainViewModel = viewModel(
//                factory = MainViewModelFactory(fbDB, weatherService)
//            )

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                val PERMISSION_REQUEST_CODE = 101
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )

                setContent {
                    val fbDB = remember { FBDatabase() }
                    val weatherService = remember { WeatherService() }

                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModel.MainViewModelFactory(
                            db = fbDB,
                            service = weatherService,
                            context = this@MainActivity
                        )
                    )

                    // Intent handler for notifications - properly indented
                    DisposableEffect(Unit) {
                        val listener = Consumer<Intent> { intent ->
                            val name = intent.getStringExtra("city")
                            val city = viewModel.cities.find { it.name == name }
                            viewModel.city = city
                            viewModel.page = Route.Home
                        }
                        addOnNewIntentListener(listener)
                        onDispose { removeOnNewIntentListener(listener) }
                    }

                    val navController = rememberNavController()
                    val currentRoute = navController.currentBackStackEntryAsState()
                    val showButton =
                        currentRoute.value?.destination?.hasRoute(Route.List::class) ?: false
                    var showDialog by remember { mutableStateOf(false) }

                    // Rest of your code remains the same...
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { granted ->
                            if (!granted) {
                                // Handle permission denial
                            }
                        }
                    )

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PermissionChecker.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }

                    WeatherAppTheme {
                        if (showDialog) {
                            CityDialog(
                                onDismiss = { showDialog = false },
                                onConfirm = { city ->
                                    if (city.isNotBlank()) viewModel.add(city)
                                    showDialog = false
                                }
                            )
                        }

                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        val name = viewModel.user?.name ?: "[não logado]"
                                        Text("Bem-vindo/a! $name")
                                    },
                                    actions = {
                                        IconButton(onClick = { finish() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                                contentDescription = "Sair"
                                            )
                                        }
                                    }
                                )
                            },
                            bottomBar = {
                                val items = listOf(
                                    BottomNavItem.HomeButton,
                                    BottomNavItem.ListButton,
                                    BottomNavItem.MapButton
                                )
                                BottomNavBar(viewModel, items)
                            },
                            floatingActionButton = {
                                if (showButton) {
                                    FloatingActionButton(onClick = { showDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                MainNavHost(navController = navController, viewModel = viewModel)
                            }
                            LaunchedEffect(viewModel.page) {
                                navController.navigate(viewModel.page) {
                                    // Volta pilha de navegação até HomePage (startDest).
                                    navController.graph.startDestinationRoute?.let {
                                        popUpTo(it) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}