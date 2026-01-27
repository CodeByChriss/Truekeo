package com.chaima.truekeo.navigation

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
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.chaima.truekeo.components.FabOverlayActions
import com.chaima.truekeo.screens.CreateProductTab
import com.chaima.truekeo.screens.HomeTab
import com.chaima.truekeo.screens.CreateTruekeTab
import com.chaima.truekeo.screens.MessagesTab
import com.chaima.truekeo.screens.ProfileTab
import com.chaima.truekeo.screens.MyTruekesTab
import com.chaima.truekeo.screens.TruekeDetailsScreen

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                fabExpanded = fabExpanded,
                onFabToggle = { fabExpanded = !fabExpanded },
                onFabClose = { fabExpanded = false }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarRoutes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavBarRoutes.Home.route) { HomeTab() }
            composable(NavBarRoutes.MyTruekes.route) {
                MyTruekesTab(navController = navController)
            }
            composable(Routes.CreateTrueke.route) { CreateTruekeTab() }
            composable(Routes.CreateProduct.route) { CreateProductTab() }
            composable(NavBarRoutes.Messages.route) { MessagesTab() }
            composable(NavBarRoutes.Profile.route) { ProfileTab() }

            composable(
                route = Routes.TruekeDetails.route,
                arguments = listOf(navArgument("truekeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val truekeId = backStackEntry.arguments?.getString("truekeId")!!
                TruekeDetailsScreen(
                    truekeId = truekeId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        FabOverlayActions(
            expanded = fabExpanded,
            onDismiss = { fabExpanded = false },
            onCreateTrueke = {
                fabExpanded = false
                if (currentRoute != Routes.CreateTrueke.route) {
                    navController.navigate(Routes.CreateTrueke.route) {
                        launchSingleTop = true
                    }
                }
            },
            onCreateProduct = {
                fabExpanded = false
                if (currentRoute != Routes.CreateProduct.route) {
                    navController.navigate(Routes.CreateProduct.route) {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}