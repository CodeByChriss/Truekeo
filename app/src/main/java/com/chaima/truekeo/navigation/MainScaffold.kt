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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chaima.truekeo.models.ChatViewModel
import com.chaima.truekeo.screens.HomeTab
import com.chaima.truekeo.screens.CreateEventTab
import com.chaima.truekeo.screens.MessageTab
import com.chaima.truekeo.screens.MessagesTab
import com.chaima.truekeo.screens.ProfileTab
import com.chaima.truekeo.screens.MyTruekesTab

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val chatViewModel: ChatViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (currentRoute != NavBarRoutes.Message.route) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarRoutes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavBarRoutes.Home.route) { HomeTab() }
            composable(NavBarRoutes.MyTruekes.route) { MyTruekesTab() }
            composable(NavBarRoutes.Create.route) { CreateEventTab() }

            composable(NavBarRoutes.Messages.route) {
                MessagesTab(
                    onMessageClick = { message ->
                        chatViewModel.onMessageSelected(message)
                        navController.navigate(NavBarRoutes.Message.route)
                    }
                )
            }

            composable(NavBarRoutes.Message.route) {
                MessageTab(
                    message = chatViewModel.selectedMessage,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavBarRoutes.Profile.route) { ProfileTab() }
        }
    }
}