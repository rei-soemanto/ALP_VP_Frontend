package com.example.alp_vp_frontend.ui.view

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_vp_frontend.R
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val posts by viewModel.posts.collectAsState()
    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pattayaFontFamily = FontFamily(
        Font(R.font.pattaya_regular)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchPosts()
        viewModel.fetchUserPosts()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Insightgram",
                    fontSize = 32.sp,
                    fontFamily = pattayaFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(posts) { post ->
                    PostCard(
                        post = post,
                        onEditClick = { id, caption, isPublic, imageUrl ->
                            val encodedUrl = Uri.encode(imageUrl)
                            val encodedCaption = Uri.encode(caption)
                            val route = "create_post_details?uri=$encodedUrl&postId=$id&caption=$caption&isPublic=$isPublic"

                            navController.navigate(route)
                        },
                        onDeleteClick = { postId ->
                            viewModel.deletePost(postId)
                        },
                        onCommentClick = {
                            selectedPostId = post.id.toInt()
                            showCommentSheet = true
                        },
                        onLikeClick = { postId, currentLikeState ->
                            viewModel.toggleLike(postId, currentLikeState)
                        },
                        editable = false
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