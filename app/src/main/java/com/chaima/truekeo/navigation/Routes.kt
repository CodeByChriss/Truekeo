package com.chaima.truekeo.navigation

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
    object TruekeDetails : Routes("trueke_details/{truekeId}") {
        fun create(truekeId: String) = "trueke_details/$truekeId"
    }
    /*object EditTrueke : NavBarRoutes("edit_trueke/{truekeId}") {
        fun create(truekeId: String) = "edit_trueke/$truekeId"
    }*/
    object CreateTrueke : Routes("create_trueke")
    object CreateProduct : Routes("create_product")
    object ProductDetails : Routes("product_details/{productId}") {
        fun create(productId: String) = "product_details/$productId"
    }
    object Messages : NavBarRoutes("messages")
    object Message : NavBarRoutes("message")
    object Profile : NavBarRoutes("profile")
    object MyProducts : NavBarRoutes("myproducts")
    object EditProfile : NavBarRoutes("edit_profile")

}