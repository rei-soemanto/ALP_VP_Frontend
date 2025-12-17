package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.alp_vp_frontend.ui.viewmodel.ChatListViewModel

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatProfile
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.theme.ImageBaseURL
import com.example.alp_vp_frontend.ui.viewmodel.ChatListUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatNavigation: (profile: ChatProfile) -> Unit,
    viewModel: ChatListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val chatList by viewModel.chatList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getChatList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat", fontWeight = FontWeight.Bold) },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (uiState is ChatListUIState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else if (uiState is ChatListUIState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.toString(),
                        color = Color.Red
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatList) { chat ->
                        ChatListItemComponent(chat) { profile ->
                            onChatNavigation(profile)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItemComponent(chat: ChatListItem, onClick: (profile: ChatProfile) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(chat.chatProfile) }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = ImageBaseURL + chat.chatProfile.avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column {
            Text(
                text = chat.chatProfile.fullName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Text(
                text = chat.read.toString(), //chat.content.trim(),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!chat.read) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),            // outer container
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xff6759FF), shape = CircleShape)
                )
            }
        }
    }
    Divider (
        modifier = Modifier.height(1.dp)
    )
}