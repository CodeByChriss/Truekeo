package com.chaima.truekeo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaima.truekeo.screens.LoginScreen
import com.chaima.truekeo.screens.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        // Pantalla 1: Splash
        composable(Routes.Splash.route) {
            SplashScreen(onNextScreen = {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            })
        }

        // Pantalla 2: Login
        composable(Routes.Login.route) {
            LoginScreen()
        }
    }
}