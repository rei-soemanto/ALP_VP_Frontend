package com.example.alp_vp_frontend.ui.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alp_vp_frontend.ui.theme.ImageBaseURL
import com.example.alp_vp_frontend.ui.viewmodel.Post

@Composable
fun PostCard(
    post: Post,
    onEditClick: (String, String, Boolean, String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onCommentClick: () -> Unit,
    onLikeClick: (String, Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray)
                ) {
                    if (!post.avatarUrl.isNullOrEmpty()) {
                        val BASE_URL = ImageBaseURL
                        val url = post.avatarUrl
                        val fullUrl = if (url.startsWith("http")) url else "$BASE_URL$url"

                        AsyncImage(
                            model = fullUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(post.date, fontSize = 12.sp, color = Color.Gray)
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.DarkGray)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Post") },
                            onClick = { showMenu = false; onEditClick(post.id, post.caption, post.isPublic, post.imageUrl) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Post", color = Color.Red) },
                            onClick = { showMenu = false; onDeleteClick(post.id) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFFE0E0E0))
            ) {
                if (post.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = "Post Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                val likeIcon = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                val likeTint = if (post.isLiked) Color.Red else Color.Black

                ActionIcon(
                    icon = likeIcon,
                    count = post.likes,
                    tint = likeTint,
                    onClick = { onLikeClick(post.id, post.isLiked) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                ActionIcon(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    count = post.comments,
                    onClick = onCommentClick
                )
            }

            ExpandableCaption(
                username = post.username,
                caption = post.caption
            )
        }
    }
}

@Composable
fun ExpandableCaption(username: String, caption: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMoreButton by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).padding(bottom = 12.dp)) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("$username ")
                }
                append(caption)
            },
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = Color.Black,
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    showMoreButton = true
                }
            },
            modifier = Modifier.animateContentSize()
        )

        if (showMoreButton && !isExpanded) {
            Text(
                text = "show more",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { isExpanded = true }
            )
        }
    }
}

@Composable
fun ActionIcon(
    icon: ImageVector,
    count: String,
    tint: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = tint)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
    }
}