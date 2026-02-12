package com.chaima.truekeo.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.screens.SplashScreen
import com.chaima.truekeo.screens.LoginScreen
import com.chaima.truekeo.screens.SignupScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val authManager = AuthContainer.authManager

    var startRoute by remember { mutableStateOf<String?>(null) }

    // Comprobamos si hay un usuario ya iniciado o no
    LaunchedEffect(Unit) {
        val isLoggedIn = authManager.checkUserSession()
        startRoute = if (isLoggedIn) Routes.Main.route else Routes.Splash.route
    }

    // Mientras decide, pantalla de carga
    if (startRoute == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startRoute!!
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
            MainScaffold(rootNavController = navController)
        }
    }
}