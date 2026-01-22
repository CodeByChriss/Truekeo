package com.chaima.truekeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chaima.truekeo.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import com.chaima.truekeo.screens.HomeTab
import com.chaima.truekeo.screens.CreateEventTab
import com.chaima.truekeo.screens.MessagesTab
import com.chaima.truekeo.screens.ProfileTab
import com.chaima.truekeo.screens.MyTruekesTab

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarRoutes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavBarRoutes.Home.route) { HomeTab() }
            composable(NavBarRoutes.MyTruekes.route) { MyTruekesTab() }
            composable(NavBarRoutes.Create.route) { CreateEventTab() }
            composable(NavBarRoutes.Messages.route) { MessagesTab() }
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
                    }
                )
            }

        }
    }
}