package com.chaima.truekeo.navigation

// Para evitar usar strings por todo el documento
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object AuthGraph : Routes("auth")
    data object Login : Routes("login")
    data object Signup : Routes("signup")
    data object Main : Routes("main")

    data object CreateTrueke : Routes("create_trueke")
    data object CreateProduct : Routes("create_product")

    data object TruekeDetails : Routes("trueke_details/{truekeId}") {
        fun create(truekeId: String) = "trueke_details/$truekeId"
    }
}

sealed class NavBarRoutes(val route: String) {
    object Home : NavBarRoutes("home")
    object MyTruekes : NavBarRoutes("mytruekes")
    object Messages : NavBarRoutes("messages")
    object Profile : NavBarRoutes("profile")
}