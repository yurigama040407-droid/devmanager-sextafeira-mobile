package com.example.devmanagerweb.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.devmanagerweb.domain.model.Perfil
import com.example.devmanagerweb.ui.screens.auth.LoginScreen
import com.example.devmanagerweb.ui.screens.auth.RegisterScreen
import com.example.devmanagerweb.ui.screens.dashboard.DashboardScreen
import com.example.devmanagerweb.ui.screens.clients.AddClientScreen
import com.example.devmanagerweb.ui.screens.clients.ClientDetailScreen
import com.example.devmanagerweb.ui.screens.clients.ClientsScreen
import com.example.devmanagerweb.ui.screens.projects.ProjectDetailScreen
import com.example.devmanagerweb.ui.screens.projects.ProjectsScreen
import com.example.devmanagerweb.ui.screens.invoices.InvoicesScreen
import com.example.devmanagerweb.ui.screens.funnel.FunnelScreen

@Composable
fun DevManagerNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { perfil ->
                    // Instead of going to Dashboard directly in the main graph,
                    // we navigate to a "MainScreen" wrapper route that passes the Profile.
                    // For simplicity, we just pass the enum string.
                    navController.navigate("main_route/${perfil.name}") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { perfil ->
                    navController.navigate("main_route/${perfil.name}") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable("main_route/{perfilName}") { backStackEntry ->
            val perfilName = backStackEntry.arguments?.getString("perfilName") ?: Perfil.CLIENTE.name
            val perfil = Perfil.valueOf(perfilName)
            MainScreen(
                userProfile = perfil,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo("main_route/{perfilName}") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun DevManagerBottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    userProfile: Perfil
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogout = onLogout,
                onNavigateToInvoices = {
                    navController.navigate(Screen.Invoices.route)
                }
            )
        }
        composable(Screen.Funnel.route) {
            FunnelScreen()
        }
        composable(Screen.Clients.route) {
            ClientsScreen(
                onNavigateToAddClient = { navController.navigate(Screen.AddClient.route) },
                onNavigateToClientDetail = { clientId -> navController.navigate(Screen.ClientDetail.createRoute(clientId)) }
            )
        }
        composable(Screen.AddClient.route) {
            AddClientScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.ClientDetail.route) {
            ClientDetailScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Projects.route) {
            ProjectsScreen(
                onNavigateToProjectDetail = { projectId -> navController.navigate(Screen.ProjectDetail.createRoute(projectId)) }
            )
        }
        composable(Screen.ProjectDetail.route) {
            ProjectDetailScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Invoices.route) {
            InvoicesScreen()
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
