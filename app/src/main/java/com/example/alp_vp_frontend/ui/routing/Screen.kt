package com.example.alp_vp_frontend.ui.routing

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alp_vp_frontend.data.dto.ChatProfile
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.view.BottomNavigationBar
import com.example.alp_vp_frontend.ui.view.ChatListScreen
import com.example.alp_vp_frontend.ui.view.ChatViewScreen
import com.example.alp_vp_frontend.ui.view.CreatePostScreen
import com.example.alp_vp_frontend.ui.view.EditProfileScreen
import com.example.alp_vp_frontend.ui.view.HomeScreen
import com.example.alp_vp_frontend.ui.view.InterestScreen
import com.example.alp_vp_frontend.ui.view.LoginScreen
import com.example.alp_vp_frontend.ui.view.MyPostsScreen
import com.example.alp_vp_frontend.ui.view.ProfileScreen
import com.example.alp_vp_frontend.ui.view.RegisterScreen
import com.example.alp_vp_frontend.ui.view.SearchScreen
import com.example.alp_vp_frontend.ui.viewmodel.ChatListViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ChatViewModel
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Interest : Screen("interest/{token}") { fun createRoute(token: String) = "interest/$token" }
    object Home : Screen("home")
    object Search : Screen("search")
    object CreatePost : Screen("create_post_tab")
    object ChatList : Screen("chat_list")
    object ChatView : Screen("chat_view/{fullName}/{id}/{avatarUrl}") {
        fun createRoute(profile: ChatProfile): String {
            val avatarUrl = (profile.avatarUrl as? String)
                ?.takeIf { it.isNotBlank() }
                ?: "none"

            return "chat_view/" +
                    Uri.encode(profile.fullName) + "/" +
                    profile.id + "/" +
                    Uri.encode(avatarUrl)
        }
    }
    object Profile : Screen("profile")
    object MyPosts : Screen("my_posts/{postId}/{filterType}") {
        fun createRoute(postId: String, filterType: String) = "my_posts/$postId/$filterType"
    }
    object EditProfile : Screen("edit_profile?name={name}&about={about}&avatar={avatar}") {
        fun createRoute(name: String, about: String, avatar: String) =
            "edit_profile?name=${Uri.encode(name)}&about=${Uri.encode(about)}&avatar=${Uri.encode(avatar)}"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)
    val tokenState = runBlocking { dataStore.tokenFlow.first() }
    val startDestination = if (!tokenState.isNullOrEmpty()) Screen.Home.route else Screen.Login.route
    val mainTabs = listOf(Screen.Home.route, Screen.Search.route, Screen.CreatePost.route, Screen.ChatList.route, Screen.Profile.route)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { if (currentRoute in mainTabs) BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination, Modifier.padding(innerPadding)) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(onNavigateToInterest = { token -> navController.navigate(Screen.Interest.createRoute(token)) })
            }
            composable(Screen.Interest.route, arguments = listOf(navArgument("token") { type = NavType.StringType })) { backStackEntry ->
                InterestScreen(token = backStackEntry.arguments?.getString("token") ?: "", onNavigateHome = { navController.navigate(Screen.Login.route) })
            }
            composable(Screen.Home.route) { HomeScreen(navController = navController) }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.CreatePost.route) {
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        navController.navigate("create_post_details?uri=${Uri.encode(uri.toString())}") { popUpTo(Screen.CreatePost.route) { inclusive = true } }
                    } else navController.popBackStack()
                }
                LaunchedEffect(Unit) { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            }

            composable(Screen.ChatList.route) { backStackEntry ->
                val viewModel: ChatListViewModel =
                    viewModel(
                        backStackEntry,
                        factory = AppViewModelProvider.Factory
                    )

                ChatListScreen(
                    onChatNavigation = { profile ->
                        navController.navigate(
                            Screen.ChatView.createRoute(profile)
                        )
                    },
                    viewModel
                )
            }

            composable(
                route = Screen.ChatView.route,
                arguments = listOf(
                    navArgument("fullName") { type = NavType.StringType },
                    navArgument("id") { type = NavType.IntType },
                    navArgument("avatarUrl") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val viewModel: ChatViewModel =
                    viewModel(
                        backStackEntry,
                        factory = AppViewModelProvider.Factory
                    )

                val fullName = backStackEntry.arguments?.getString("fullName")!!
                val id = backStackEntry.arguments?.getInt("id")!!
                val avatarUrl = backStackEntry.arguments?.getString("avatarUrl")!!

                ChatViewScreen(
                    counterPartId = id,
                    profileFullName = fullName,
                    profileAvatarUrl = avatarUrl,
                    onBack = {
                        navController.popBackStack()
                    },
                    viewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                    onPostClick = { postId, filterType ->
                        navController.navigate(Screen.MyPosts.createRoute(postId, filterType))
                    },
                    onEditProfileClick = { name, about, avatar ->
                        navController.navigate(Screen.EditProfile.createRoute(name, about, avatar))
                    }
                )
            }

            composable(
                route = Screen.EditProfile.route,
                arguments = listOf(
                    navArgument("name") { defaultValue = "" },
                    navArgument("about") { defaultValue = "" },
                    navArgument("avatar") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val about = backStackEntry.arguments?.getString("about") ?: ""
                val avatar = backStackEntry.arguments?.getString("avatar") ?: ""

                EditProfileScreen(
                    currentName = name,
                    currentAbout = about,
                    currentAvatarUrl = avatar,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.MyPosts.route,
                arguments = listOf(
                    navArgument("postId") { type = NavType.StringType },
                    navArgument("filterType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val initialPostId = backStackEntry.arguments?.getString("postId")
                val filterType = backStackEntry.arguments?.getString("filterType")

                MyPostsScreen(
                    navController = navController,
//                    viewModel = postViewModel,
                    initialPostId = initialPostId,
                    filterType = filterType
                )
            }

            composable(
                route = "create_post_details?uri={uri}&postId={postId}&caption={caption}&isPublic={isPublic}",
                arguments = listOf(
                    navArgument("uri") { type = NavType.StringType },
                    navArgument("postId") { nullable = true; defaultValue = null },
                    navArgument("caption") { nullable = true; defaultValue = "" },
                    navArgument("isPublic") { type = NavType.BoolType; defaultValue = true }
                )
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("uri")
                val postId = backStackEntry.arguments?.getString("postId")
                val initialCaption = backStackEntry.arguments?.getString("caption") ?: ""
                val initialIsPublic = backStackEntry.arguments?.getBoolean("isPublic") ?: true
                val imageUri = uriString?.let { Uri.parse(it) }

                CreatePostScreen(
                    imageUri = imageUri,
                    postId = postId,
                    initialCaption = initialCaption,
                    initialIsPublic = initialIsPublic,
                    onBackClick = { navController.popBackStack() },
                    onShareClick = { // caption, isPublic ->
//                        if (postId == null) postViewModel.createPost(context, caption, imageUri, isPublic)
//                        else postViewModel.updatePost(postId, caption, isPublic)
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                    }
                )
            }
        }
    }
}