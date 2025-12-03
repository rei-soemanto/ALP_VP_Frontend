package com.example.alp_vp_frontend.ui.routing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alp_vp_frontend.ui.view.InterestScreen
import com.example.alp_vp_frontend.ui.view.RegisterScreen
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.ui.view.BottomNavigationBar
import com.example.alp_vp_frontend.ui.view.ChatScreen
import com.example.alp_vp_frontend.ui.view.CreatePostScreen
import com.example.alp_vp_frontend.ui.view.HomeScreen
import com.example.alp_vp_frontend.ui.view.LoginScreen
import com.example.alp_vp_frontend.ui.view.ProfileScreen
import com.example.alp_vp_frontend.ui.view.SearchScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Interest : Screen("interest/{token}") {
        fun createRoute(token: String) = "interest/$token"
    }

    object Home : Screen("home")
    object Search : Screen("search")
    object CreatePost : Screen("create_post")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dataStore = DataStoreManager(context)
    val tokenState = runBlocking { dataStore.tokenFlow.first() }
    val startDestination = if (!tokenState.isNullOrEmpty()) Screen.Home.route else Screen.Login.route

    val mainTabs = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.CreatePost.route,
        Screen.Chat.route,
        Screen.Profile.route
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in mainTabs) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
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

            composable(Screen.Home.route) { HomeScreen() }

            composable(Screen.Search.route) { SearchScreen() }

            composable(Screen.CreatePost.route) { CreatePostScreen() }

            composable(Screen.Chat.route) { ChatScreen() }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}