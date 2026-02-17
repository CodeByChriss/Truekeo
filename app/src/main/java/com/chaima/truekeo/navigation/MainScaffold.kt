package com.chaima.truekeo.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chaima.truekeo.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.chaima.truekeo.components.FabOverlayActions
import com.chaima.truekeo.screens.CreateProductTab
import com.chaima.truekeo.screens.HomeTab
import com.chaima.truekeo.screens.CreateTruekeTab
import androidx.navigation.NavController
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.screens.MessageScreen
import com.chaima.truekeo.screens.EditProfileScreen
import com.chaima.truekeo.screens.MessagesTab
import com.chaima.truekeo.screens.MyProductsScreen
import com.chaima.truekeo.screens.ProfileTab
import com.chaima.truekeo.screens.MyTruekesTab
import com.chaima.truekeo.screens.ProductDetailsScreen
import com.chaima.truekeo.screens.TruekeDetailsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScaffold(rootNavController: NavController) {
    val authManager = AuthContainer.authManager
    val context = LocalContext.current

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute != "${NavBarRoutes.Message.route}/{conversationId}") {
                BottomNavBar(
                    navController = navController,
                    onFabToggle = { fabExpanded = !fabExpanded },
                    onFabClose = { fabExpanded = false }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarRoutes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavBarRoutes.Home.route) {
                HomeTab(
                    onOpenConversation = { conversationId ->
                        navController.navigate("${NavBarRoutes.Message.route}/$conversationId")
                    }
                )
            }
            composable(NavBarRoutes.MyTruekes.route) {
                MyTruekesTab(navController = navController)
            }

            composable(NavBarRoutes.CreateTrueke.route) {
                CreateTruekeTab(navController = navController)
            }
            composable(NavBarRoutes.CreateProduct.route) { CreateProductTab() }

            composable(
                route = NavBarRoutes.TruekeDetails.route,
                arguments = listOf(navArgument("truekeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val truekeId = backStackEntry.arguments?.getString("truekeId")!!
                TruekeDetailsScreen(
                    truekeId = truekeId,
                    onBack = { navController.popBackStack() },
                    onOpenConversation = { conversationId ->
                        navController.navigate("${NavBarRoutes.Message.route}/$conversationId")
                    }
                )
            }

            /*composable(
                route = NavBarRoutes.EditTrueke.route,
                arguments = listOf(navArgument("truekeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val truekeId = backStackEntry.arguments?.getString("truekeId")!!
                EditTruekeScreen(
                    truekeId = truekeId,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }*/
            
            composable(NavBarRoutes.Messages.route) {
                MessagesTab(
                    onMessageClick = { conversationId ->
                        navController.navigate("${NavBarRoutes.Message.route}/$conversationId")
                    }
                )
            }

            composable(
                route = "${NavBarRoutes.Message.route}/{conversationId}",
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId")
                MessageScreen(
                    conversationId = conversationId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavBarRoutes.Profile.route) {
                ProfileTab(
                    onMyTruekesClick = {
                        navController.navigate(NavBarRoutes.MyTruekes.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMessagesClick = {
                        navController.navigate(NavBarRoutes.Messages.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMyProductsClick = {
                        navController.navigate(NavBarRoutes.MyProducts.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onEditProfileClick = {
                        navController.navigate(NavBarRoutes.EditProfile.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogoutClick = {
                        authManager.logout(context)

                        // limpia el backstack del nav interno
                        navController.popBackStack(navController.graph.startDestinationId, inclusive = false)

                        rootNavController.navigate(Routes.AuthGraph.route) {
                            popUpTo(Routes.Main.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(NavBarRoutes.MyProducts.route) {
                MyProductsScreen(navController)
            }


            composable(
                route = "product_details/{productName}",
                arguments = listOf(
                    navArgument("productName") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val productName =
                    backStackEntry.arguments?.getString("productName") ?: ""

                ProductDetailsScreen(
                    productName = productName,
                    onBack = { navController.popBackStack() }
                )
            }



            composable(NavBarRoutes.EditProfile.route) {
                EditProfileScreen(
                    onCloseClick = {
                        navController.navigate(NavBarRoutes.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSaveChangesClick = {
                        navController.navigate(NavBarRoutes.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

        }

        FabOverlayActions(
            expanded = fabExpanded,
            onDismiss = { fabExpanded = false },
            onCreateTrueke = {
                fabExpanded = false
                if (currentRoute != NavBarRoutes.CreateTrueke.route) {
                    navController.navigate(NavBarRoutes.CreateTrueke.route) {
                        launchSingleTop = true
                    }
                }
            },
            onCreateProduct = {
                fabExpanded = false
                if (currentRoute != NavBarRoutes.CreateProduct.route) {
                    navController.navigate(NavBarRoutes.CreateProduct.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}