package com.example.devmanagerweb.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.devmanagerweb.domain.model.Perfil

sealed class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, "Painel", Icons.Default.Home)
    object Funnel : BottomNavItem(Screen.Funnel.route, "Funil", Icons.Default.List)
    object Clients : BottomNavItem(Screen.Clients.route, "Clientes", Icons.Default.Person)
    object Projects : BottomNavItem(Screen.Projects.route, "Projetos", Icons.Default.Build)
    object Invoices : BottomNavItem(Screen.Invoices.route, "Faturas", Icons.Default.ShoppingCart)
}

@Composable
fun MainScreen(
    userProfile: Perfil,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    
    // Perfil Desenvolvedor: Painel | Funil | Clientes | Projetos | Faturas
    // Perfil Cliente: Painel | Projetos | Faturas
    val items = if (userProfile == Perfil.DESENVOLVEDOR) {
        listOf(
            BottomNavItem.Dashboard,
            BottomNavItem.Funnel,
            BottomNavItem.Clients,
            BottomNavItem.Projects,
            BottomNavItem.Invoices
        )
    } else {
        listOf(
            BottomNavItem.Dashboard,
            BottomNavItem.Projects,
            BottomNavItem.Invoices
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        // We nest a NavHost here specifically for the bottom bar content
        DevManagerBottomNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogout = onLogout,
            userProfile = userProfile
        )
    }
}
