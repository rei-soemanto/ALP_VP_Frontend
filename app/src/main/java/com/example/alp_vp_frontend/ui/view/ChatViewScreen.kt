package com.example.alp_vp_frontend.ui.view

import android.R.attr.maxLines
import android.R.attr.minLines
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.theme.ImageBaseURL
import com.example.alp_vp_frontend.ui.viewmodel.ChatViewModel
import com.smarttoolfactory.bubble.ArrowAlignment
import com.smarttoolfactory.bubble.ArrowShape
import com.smarttoolfactory.bubble.BubbleCornerRadius
import com.smarttoolfactory.bubble.BubbleState
import com.smarttoolfactory.bubble.bubble
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatTimestamp(raw: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(raw)
        val deviceZone = ZoneId.systemDefault()
        val localDateTime = zonedDateTime.withZoneSameInstant(deviceZone)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        raw
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatViewScreen(
    counterPartId: Int,
    profileFullName: String,
    profileAvatarUrl: String?,
    onBack: () -> Unit,
    onViewImage: (messageId: Int) -> Unit,
    viewModel: ChatViewModel = viewModel(factory = AppViewModelProvider.Factory),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current

    val messages by viewModel.messages.collectAsState()
    var message by remember { mutableStateOf("")}
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMessages(counterPartId)
        viewModel.connectSocket(counterPartId)
        viewModel.runListeners(counterPartId)
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
                                .fillMaxHeight(0.8f)
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
                    IconButton(
                        onClick = {
                            onBack()
                        }
                    ) {
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
                itemsIndexed(messages) { index, msg ->
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = if (msg.senderId == counterPartId) {
                            Alignment.Start
                        } else {
                            Alignment.End
                        }
                    ) {
                        val isIncoming = msg.senderId == counterPartId

                        Column(
                            modifier = Modifier
                                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.8f)
                                .width(IntrinsicSize.Min)
                                .bubble(
                                    bubbleState = BubbleState(
                                        alignment = if (isIncoming)
                                            ArrowAlignment.LeftTop
                                        else
                                            ArrowAlignment.RightTop,
                                        arrowShape = ArrowShape.HalfTriangle,
                                        drawArrow = true
                                    ),
                                    color = if (isIncoming)
                                        Color(0xffD9D9D9)
                                    else
                                        Color(0xff6759FF)
                                )
                                .padding(8.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (msg.images.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp * 0.6f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    AsyncImage(
                                        model = ImageBaseURL + msg.images[0],
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                onViewImage(msg.id)
                                            }
                                    )

                                    Text(
                                        text = "${msg.images.size} images",
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .background(
                                                color = Color.Black.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (msg.content.isNotBlank()) {
                                Text(
                                    text = msg.content,
                                    fontSize = 14.sp,
                                    color = if (isIncoming) Color.Black else Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(min = 105.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = formatTimestamp(msg.timestamp),
                                        fontSize = 10.sp,
                                        color = if (isIncoming) Color.Black else Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )

                                    if (!isIncoming) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = null,
                                            tint = if (msg.read) Color.White else Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedImageUris) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
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
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                }

                IconButton(
                    onClick = {
                        viewModel.sendMessage(context, counterPartId, message, selectedImageUris)
                        message = ""
                        selectedImageUris = emptyList()
                    },
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