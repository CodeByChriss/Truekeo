package com.chaima.truekeo.navigation

// Para evitar usar strings por todo el documento
sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Login : Routes("login")
}