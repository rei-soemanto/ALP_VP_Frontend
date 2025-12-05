package com.example.alp_vp_frontend.ui.view

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // ✅ Import this
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel

@Composable
fun HomeScreen(
    viewModel: PostViewModel = viewModel(),
    navController: NavController
) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Insightgram",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
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
                        onPostClick = { /* Handle Detail View */ }
                    )
                }
            }
        }
    }
}