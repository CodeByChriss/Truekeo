package com.chaima.truekeo.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chaima.truekeo.navigation.NavBarRoutes
import androidx.compose.ui.graphics.Color

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    val items = listOf(
        BottomItem(
            route = NavBarRoutes.Home.route,
            label = "Home",
            icon = { Icon(Icons.Filled.Check, contentDescription = "Home") }
        ),
        BottomItem(
            route = NavBarRoutes.MyTruekes.route,
            label = "My Truekes",
            icon = { Icon(Icons.Filled.Check, contentDescription = "My Truekes") }
        ),
        BottomItem(
            route = NavBarRoutes.Create.route,
            label = "Create",
            icon = { Icon(Icons.Filled.Check, contentDescription = "Create") }
        ),
        BottomItem(
            route = NavBarRoutes.Messages.route,
            label = "Messages",
            icon = { Icon(Icons.Filled.Check, contentDescription = "Messages") }
        ),
        BottomItem(
            route = NavBarRoutes.Profile.route,
            label = "Profile",
            icon = { Icon(Icons.Filled.Check, contentDescription = "Profile") }
        )
    )

    NavigationBar(
        containerColor = Color(0xFFFFFFFF)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // evita duplicar destinos al tocar varias veces
                            launchSingleTop = true
                            // mantiene estado al volver a una pestaña
                            restoreState = true
                            // hace pop al inicio del grafo del bottom nav
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = item.icon,
                //label = { Text(item.label) } ASÍ NO SALE TEXTO EN LOS BOTONES
            )
        }
    }
}