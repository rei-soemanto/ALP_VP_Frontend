package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.theme.ImageBaseURL
import com.example.alp_vp_frontend.ui.viewmodel.ChatViewModel
import com.smarttoolfactory.bubble.ArrowAlignment
import com.smarttoolfactory.bubble.BubbleCornerRadius
import com.smarttoolfactory.bubble.BubbleState
import com.smarttoolfactory.bubble.bubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatViewScreen(
    counterPartId: Int,
    profileFullName: String,
    profileAvatarUrl: String?,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val messages by viewModel.messages.collectAsState()
    var message by remember { mutableStateOf("")}

    LaunchedEffect(Unit) {
        viewModel.getMessages(counterPartId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = ImageBaseURL + profileAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Text(
                            text = profileFullName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = if (msg.senderId == counterPartId) {
                            Arrangement.Start
                        } else {
                            Arrangement.End
                        }
                    ) {
//                        Spacer(modifier = Modifier.fillMaxWidth(0.2f))

                        Column(
                            modifier = Modifier
                                .bubble(
                                    bubbleState = BubbleState(
                                        cornerRadius = BubbleCornerRadius(5.dp),
                                        alignment = if (msg.senderId == counterPartId) {
                                            ArrowAlignment.LeftTop
                                        } else {
                                            ArrowAlignment.RightTop
                                        },
                                    )
                                )
                                .background(if (msg.senderId == counterPartId) Color(0xff6759FF) else Color(0xffD9D9D9))
                        ) {
                            Text(
                                text = msg.content,
                                fontSize = 14.sp,
                                color = if (msg.senderId == counterPartId) Color.Black else Color.White
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 10.sp,
                                    color = if (msg.senderId == counterPartId) Color.Black else Color.White
                                )
                                Spacer(modifier = Modifier.width(12.dp))

                                if (msg.senderId != counterPartId) {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = null,
                                        tint = if (msg.read) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    placeholder = { Text(text = "Message") },
                    onValueChange = { message = it },
                    label = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 1,
                    maxLines = 4
                )

                IconButton(
                    modifier = Modifier
                        .size(52.dp),
                    onClick = {}
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                }

                IconButton(
                    onClick = { /* handle click */ },
                    modifier = Modifier
                        .size(52.dp) // total size of the circle
                        .background(color = Color(0xFF6759FF), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.White // icon color
                    )
                }
            }
        }
    }
}