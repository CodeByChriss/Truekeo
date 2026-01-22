package com.chaima.truekeo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
        // Pantalla de Splash
        composable(Routes.Splash.route) {
            SplashScreen(onNextScreen = {
                navController.navigate(Routes.AuthGraph.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        // Navegación entre pantallas de autenticación
        navigation(
            route = Routes.AuthGraph.route,
            startDestination = Routes.Login.route
        ) {

            // Pantalla de Login
            composable(Routes.Login.route) {
                LoginScreen(
                    onGoToSignup = {
                        navController.navigate(Routes.Signup.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogin = {
                        navController.navigate(Routes.Main.route) {
                            popUpTo(Routes.AuthGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Pantalla de SignUp
            composable(Routes.Signup.route) {
                SignupScreen(
                    onSignUp = {
                        navController.navigate(Routes.Main.route) {
                            popUpTo(Routes.AuthGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack() // vuelve al Login
                    }
                )
            }
        }

        // Navegación entre pantallas una vez autenticado
        composable(Routes.Main.route) {
            MainScaffold()
        }
    }
}