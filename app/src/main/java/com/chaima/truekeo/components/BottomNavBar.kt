package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chaima.truekeo.navigation.NavBarRoutes
import com.chaima.truekeo.navigation.Routes

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(
    navController: NavController,
    onFabToggle: () -> Unit,
    onFabClose: () -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    val isInTruekeDetails = currentRoute?.startsWith("trueke_details") == true

    val leftItems = listOf(
        BottomItem(
            route = NavBarRoutes.Home.route,
            label = "Home",
            icon = {
                Icon(
                    Icons.Rounded.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp)
                )
            }
        ),
        BottomItem(
            route = NavBarRoutes.MyTruekes.route,
            label = "My Truekes",
            icon = {
                Icon(
                    Icons.AutoMirrored.Rounded.EventNote,
                    contentDescription = "My Truekes",
                    modifier = Modifier.size(26.dp)
                )
            }
        )
    )

    val rightItems = listOf(
        BottomItem(
            route = NavBarRoutes.Messages.route,
            label = "Messages",
            icon = {
                Icon(
                    Icons.AutoMirrored.Rounded.Chat,
                    contentDescription = "Messages",
                    modifier = Modifier.size(26.dp)
                )
            }
        ),
        BottomItem(
            route = NavBarRoutes.Profile.route,
            label = "Profile",
            icon = {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(26.dp)
                )
            }
        )
    )

    fun goToTab(route: String) {
        onFabClose()

        navController.popBackStack(NavBarRoutes.CreateTrueke.route, inclusive = true)
        navController.popBackStack(NavBarRoutes.CreateProduct.route, inclusive = true)

        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            // Items de la izquierda
            leftItems.forEach { item ->
                val selected = when {
                    isInTruekeDetails && item.route == NavBarRoutes.MyTruekes.route -> true
                    else -> currentRoute == item.route
                }

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        // Caso especial para que desde detaille de un trueke, al pulsar el tab de mis truekes vuelve al padre
                        if (isInTruekeDetails && item.route == NavBarRoutes.MyTruekes.route) {
                            onFabClose()
                            val popped = navController.popBackStack(NavBarRoutes.MyTruekes.route, inclusive = false)
                            if (!popped) goToTab(NavBarRoutes.MyTruekes.route)
                            return@NavigationBarItem
                        }

                        if (currentRoute != item.route) {
                            goToTab(item.route)
                        }
                    },
                    icon = item.icon,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }

            // Espacio para el botón central (sin peso)
            Box(
                modifier = Modifier.width(72.dp),
                contentAlignment = Alignment.Center
            ) {
                // Espacio vacío para el botón
            }

            // Items de la derecha
            rightItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            goToTab(item.route)
                        }
                    },
                    icon = item.icon,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }

        // Botón central integrado
        FloatingActionButton(
            onClick = onFabToggle,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(60.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Create",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}