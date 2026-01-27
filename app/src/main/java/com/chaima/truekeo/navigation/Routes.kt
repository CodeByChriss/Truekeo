package com.chaima.truekeo.navigation

// Para evitar usar strings por todo el documento
sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Login : Routes("login")
    object Signup : Routes("signup")
    object Main : Routes("main")
}

sealed class NavBarRoutes(val route: String) {
    object Home : NavBarRoutes("home")
    object MyTruekes : NavBarRoutes("mytruekes")
    object Create : NavBarRoutes("create")
    object Messages : NavBarRoutes("messages")
    object Message : NavBarRoutes("message")
    object Profile : NavBarRoutes("profile")
    object MyProducts : NavBarRoutes("myproducts")
    object EditProfile : NavBarRoutes("edit_profile")


}