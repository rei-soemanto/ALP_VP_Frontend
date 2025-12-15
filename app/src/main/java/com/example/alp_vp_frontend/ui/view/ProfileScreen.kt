package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.viewmodel.AuthViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ProfileViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onLogout: () -> Unit,
    onPostClick: (String, String) -> Unit,
    onEditProfileClick: (String, String, String) -> Unit
) {
    val state = profileViewModel.profileState

    LaunchedEffect(Unit) {
        profileViewModel.fetchProfileData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { authViewModel.logout(onLogout) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            when (state) {
                is ProfileUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                is ProfileUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = Color.Red)
                        Button(onClick = { profileViewModel.fetchProfileData() }) { Text("Retry") }
                    }
                }
                is ProfileUiState.Success -> ProfileContent(
                    user = state.user,
                    posts = state.posts,
                    selectedTab = profileViewModel.selectedTabIndex,
                    onTabSelected = { profileViewModel.selectedTabIndex = it },
                    onPostClick = onPostClick,
                    onEditProfileClick = onEditProfileClick
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: UserResponse,
    posts: List<PostResponse>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onPostClick: (String, String) -> Unit,
    onEditProfileClick: (String, String, String) -> Unit
) {
    val filteredPosts = if (selectedTab == 0) {
        posts.filter { it.isPublic }
    } else {
        posts.filter { !it.isPublic }
    }

    val currentFilter = if (selectedTab == 0) "public" else "private"

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                if (!user.avatarUrl.isNullOrEmpty()) {
                    val BASE_URL = "http://10.0.2.2:3000"
                    val fullUrl = if (user.avatarUrl.startsWith("http")) user.avatarUrl else "$BASE_URL${user.avatarUrl}"

                    AsyncImage(
                        model = fullUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatItem(user.postsCount ?: 0, "posts")
                    StatItem(user.followersCount ?: 0, "followers")
                    StatItem(user.followingCount ?: 0, "following")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (!user.about.isNullOrEmpty()) {
                    Text(text = user.about, modifier = Modifier.padding(vertical = 8.dp), fontSize = 14.sp)
                }
            }
        }
        OutlinedButton(
            onClick = {
                onEditProfileClick(
                    user.fullName ?: "",
                    user.about ?: "",
                    user.avatarUrl ?: ""
                )
            },
            modifier = Modifier.fillMaxWidth().height(36.dp).padding(horizontal = 20.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Edit Profile", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.Black
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Public Post") },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Private Post") },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
        }

        if (filteredPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No posts yet", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(filteredPosts) { post ->
                    PostGridItem(
                        post = post,
                        onClick = { onPostClick(post.id.toString(), currentFilter) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun PostGridItem(
    post: PostResponse,
    onClick: () -> Unit,
) {
    val firstImage = post.images?.firstOrNull()?.imageUrl
    val BASE_URL = "http://10.0.2.2:3000"

    val fullImageUrl = if (firstImage?.startsWith("http") == true) {
        firstImage
    } else {
        "$BASE_URL${firstImage ?: ""}"
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.LightGray)
            .clickable { onClick() }
    ) {
        if (firstImage != null) {
            AsyncImage(
                model = fullImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}