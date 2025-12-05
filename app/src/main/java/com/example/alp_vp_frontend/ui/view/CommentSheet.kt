package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_vp_frontend.data.dto.CommentResponse
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.viewmodel.CommentUiState
import com.example.alp_vp_frontend.ui.viewmodel.CommentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheet(
    postId: Int,
    onDismiss: () -> Unit,
    viewModel: CommentViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.fetchComments(postId)
    }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        },
        bottomBar = {
            CommentInputBar(
                text = commentText,
                onTextChange = { commentText = it },
                replyingTo = viewModel.replyingToComment,
                onCancelReply = { viewModel.setReplyingTo(null) },
                onSend = {
                    if (commentText.isNotBlank()) {
                        viewModel.postComment(postId, commentText) {
                            commentText = "" // Clear input on success
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is CommentUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is CommentUiState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                is CommentUiState.Success -> {
                    if (state.comments.isEmpty()) {
                        Text("No comments yet.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.comments) { comment ->
                                CommentItem(comment, onReplyClick = { viewModel.setReplyingTo(comment) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentResponse, onReplyClick: (CommentResponse) -> Unit, isReply: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .padding(start = if (isReply) 40.dp else 0.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)) {
                if (!comment.author.avatarUrl.isNullOrEmpty()) {
                    val BASE_URL = "http://10.0.2.2:3000"
                    val url = comment.author.avatarUrl
                    val fullUrl = if (url.startsWith("http")) url else "$BASE_URL$url"

                    AsyncImage(
                        model = fullUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comment.author.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(comment.content, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "Reply",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onReplyClick(comment) }
                    )
                }
            }
        }

        comment.replies?.forEach { reply ->
            CommentItem(comment = reply, onReplyClick = { onReplyClick(comment) }, isReply = true)
        }
    }
}

@Composable
fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    replyingTo: CommentResponse?,
    onCancelReply: () -> Unit,
    onSend: () -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        if (replyingTo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Replying to ${replyingTo.author.fullName}", color = Color.Gray, fontSize = 12.sp)
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel reply",
                    modifier = Modifier.size(16.dp).clickable { onCancelReply() },
                    tint = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)) // Current User Avatar

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(if (replyingTo != null) "Reply..." else "Add a comment...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                ),
                maxLines = 3
            )

            IconButton(onClick = onSend, enabled = text.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF6C5CE7))
            }
        }
    }
}