package com.chaima.truekeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chaima.truekeo.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import com.chaima.truekeo.screens.CreateEventScreen
import com.chaima.truekeo.screens.HomeScreen
import com.chaima.truekeo.screens.MessagesScreen
import com.chaima.truekeo.screens.ProfileScreen
import com.chaima.truekeo.screens.MyTruekesScreen

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
            composable(NavBarRoutes.Home.route) { HomeScreen() }
            composable(NavBarRoutes.MyTruekes.route) { MyTruekesScreen() }
            composable(NavBarRoutes.Create.route) { CreateEventScreen() }
            composable(NavBarRoutes.Messages.route) { MessagesScreen() }
            composable(NavBarRoutes.Profile.route) { ProfileScreen() }
        }
    }
}