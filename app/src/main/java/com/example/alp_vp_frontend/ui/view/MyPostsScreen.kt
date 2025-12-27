package com.example.alp_vp_frontend.ui.view

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    navController: NavController,
    initialPostId: String?,
    filterType: String?,
    viewModel: PostViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userPosts by viewModel.userPosts.collectAsState()
    val displayedPosts = remember(userPosts, filterType) {
        when (filterType) {
            "public" -> userPosts.filter { it.isPublic }
            "private" -> userPosts.filter { !it.isPublic }
            else -> userPosts
        }
    }
    val screenTitle = if (filterType == "public") "Public Posts" else "Private Posts"
    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    LaunchedEffect(initialPostId, userPosts) {
        if (initialPostId != null && userPosts.isNotEmpty()) {
            val index = userPosts.indexOfFirst { it.id == initialPostId }
            if (index >= 0) {
                listState.scrollToItem(index)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (userPosts.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No posts found")
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(displayedPosts) { post ->
                    PostCard(
                        post = post,
                        onEditClick = { id, caption, isPublic, imageUrl ->
                            val encodedUrl = Uri.encode(imageUrl)
                            val route = "create_post_details?uri=$encodedUrl&postId=$id&caption=$caption&isPublic=$isPublic"
                            navController.navigate(route)
                        },
                        onDeleteClick = { viewModel.deletePost(it) },
                        onCommentClick = {
                            selectedPostId = post.id.toInt()
                            showCommentSheet = true
                        },
                        onLikeClick = { postId, currentLikeState ->
                            viewModel.toggleLike(postId, currentLikeState)
                        }
                    )
                }

            }

            if (showCommentSheet && selectedPostId != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showCommentSheet = false
                        selectedPostId = null
                    },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    Box(modifier = Modifier.fillMaxHeight(0.7f)) {
                        CommentSheet(
                            postId = selectedPostId!!,
                            onDismiss = { showCommentSheet = false }
                        )
                    }
                }
            }
        }
    }
}