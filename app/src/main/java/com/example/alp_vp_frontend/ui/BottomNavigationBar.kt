package com.example.alp_vp_frontend.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.alp_vp_frontend.ui.routing.Screen

val PurpleActive = Color(0xFF6759FF)
val BlackInactive = Color.Black

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
        BottomNavItem(Screen.Search.route, Icons.Default.Search, "Search"),
        BottomNavItem(Screen.CreatePost.route, Icons.Default.Add, "Post"),
        BottomNavItem(Screen.ChatList.route, Icons.Default.ChatBubbleOutline, "Chat"),
        BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleActive,
                    unselectedIconColor = BlackInactive,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}