package com.example.alp_vp_frontend.ui.view

import android.util.Log.v
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.theme.ImageBaseURL
import com.example.alp_vp_frontend.ui.viewmodel.MessageImageViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MessageImageScreen(
    messageId: Int,
    onBack: () -> Unit,
    viewModel: MessageImageViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val images = viewModel.imageURLs.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { images.value.size }
    )

    LaunchedEffect(Unit) {
        viewModel.getImages(messageId)
    }

    LaunchedEffect(pagerState) {
        println(pagerState.currentPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Back",
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            ) { page ->
                AsyncImage(
                    model = ImageBaseURL + images.value.get(page),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.value.size) { index ->
                   Box(
                       modifier = Modifier
                           .size(16.dp)
                           .background(
                               if (pagerState.currentPage == index) Color.Black else Color.Gray,
                               CircleShape
                           )
                   )
                }
            }
        }
    }
}