package com.chaima.truekeo.navigation

// Para evitar usar strings por todo el documento
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object AuthGraph : Routes("auth")
    data object Login : Routes("login")
    data object Signup : Routes("signup")
    data object Main : Routes("main")
}

sealed class NavBarRoutes(val route: String) {
    object Home : NavBarRoutes("home")
    object MyTruekes : NavBarRoutes("mytruekes")
    object Create : NavBarRoutes("create")
    object Messages : NavBarRoutes("messages")
    object Profile : NavBarRoutes("profile")
}