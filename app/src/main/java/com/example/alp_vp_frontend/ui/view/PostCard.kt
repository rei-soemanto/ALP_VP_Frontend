package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alp_vp_frontend.ui.viewmodel.Post

@Composable
fun PostCard(
    post: Post,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onPostClick: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onPostClick(post.id) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        AsyncImage(
                            model = post.avatarUrl,
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
                            onClick = { showMenu = false; onEditClick(post.id) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Post", color = Color.Red) },
                            onClick = { showMenu = false; onDeleteClick(post.id) }
                        )
                    }
                }
            }

            // FIXED: Using imageUrl
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
                ActionIcon(Icons.Outlined.FavoriteBorder, post.likes) {}
                Spacer(modifier = Modifier.width(16.dp))
                ActionIcon(Icons.Outlined.ChatBubbleOutline, post.comments) {}
            }

            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).padding(bottom = 12.dp),
                text = buildAnnotatedString {
                    append(post.caption)
                    withStyle(style = SpanStyle(color = Color.Gray)) { append(" selengkapnya") }
                },
                fontSize = 14.sp, lineHeight = 18.sp, color = Color.Black
            )
        }
    }
}

@Composable
fun ActionIcon(icon: ImageVector, count: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = Color.Black)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
    }
}