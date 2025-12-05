package com.example.alp_vp_frontend.ui.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.AsyncImage

val Blurple = Color(0xFF6C5CE7)
val LightGrey = Color(0xFFE0E0E0)
val InputGrey = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    imageUri: Uri?,
    postId: String? = null,
    initialCaption: String = "",
    initialIsPublic: Boolean = true,

    onBackClick: () -> Unit,
    onShareClick: (String, Boolean) -> Unit
) {
    var caption by remember(initialCaption) { mutableStateOf(initialCaption) }
    var isPublic by remember(initialIsPublic) { mutableStateOf(initialIsPublic) }

    val isEditMode = postId != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Post" else "New post",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { onShareClick(caption, isPublic) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isEditMode) "Update" else "Share",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .aspectRatio(0.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightGrey)
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Add caption ...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)).background(InputGrey),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputGrey, unfocusedContainerColor = InputGrey,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrivacyOptionRow("Send Post to Public", isPublic) { isPublic = true }
            Spacer(modifier = Modifier.height(16.dp))
            PrivacyOptionRow("Send Post to Private", !isPublic) { isPublic = false }
        }
    }
}

@Composable
fun PrivacyOptionRow(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(if (isSelected) Blurple else LightGrey),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}