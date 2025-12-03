package com.example.alp_vp_frontend.ui.routing

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alp_vp_frontend.ui.view.InterestScreen
import com.example.alp_vp_frontend.ui.view.RegisterScreen
import androidx.compose.material3.Text

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Interest : Screen("interest/{token}") {
        fun createRoute(token: String) = "interest/$token"
    }
    object Login : Screen("login")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Register.route
    ) {
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

        composable(Screen.Login.route) {
            Text(text = "Login Screen Placeholder")
        }
    }
}