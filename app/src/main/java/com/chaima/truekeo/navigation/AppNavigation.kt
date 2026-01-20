package com.chaima.truekeo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaima.truekeo.screens.SplashScreen
import com.chaima.truekeo.screens.LoginScreen
import com.chaima.truekeo.screens.SignupScreen

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
            LoginScreen(
                onSignUp = {
                    navController.navigate(Routes.Signup.route) {
                        popUpTo(Routes.Login.route) { inclusive = false }
                    }
                },
                onLogin = {
                    navController.navigate(Routes.Main.route){
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 3: Signup
        composable(Routes.Signup.route) {
            SignupScreen(
                onSignUp = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Signup.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // Navegación entre pantallas una vez autenticado
        composable(Routes.Main.route) {
            MainScaffold()
        }
    }
}