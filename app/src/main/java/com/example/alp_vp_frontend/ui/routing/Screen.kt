package com.example.alp_vp_frontend.ui.routing

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alp_vp_frontend.ui.view.InterestScreen
import com.example.alp_vp_frontend.ui.view.RegisterScreen
import androidx.compose.ui.platform.LocalContext
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.ui.view.HomeScreen
import com.example.alp_vp_frontend.ui.view.LoginScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Interest : Screen("interest/{token}") {
        fun createRoute(token: String) = "interest/$token"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dataStore = DataStoreManager(context)
    val tokenState = runBlocking { dataStore.tokenFlow.first() }
    val startDestination = if (!tokenState.isNullOrEmpty()) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToInterest = { token ->
                    navController.navigate(Screen.Interest.createRoute(token))
                },
            )
        }

        composable(
            route = Screen.Interest.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""

            InterestScreen(
                token = token,
                onNavigateHome = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}