package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_vp_frontend.ui.AppViewModelProvider
import com.example.alp_vp_frontend.ui.viewmodel.AuthUiState
import com.example.alp_vp_frontend.ui.viewmodel.AuthViewModel

@Composable
fun InterestScreen(
    token: String,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateHome: () -> Unit
) {
    val interests = viewModel.interestList
    val selectedInterests = viewModel.selectedInterests
    val uiState = viewModel.authState
    val mainColor = Color(0xFF6759FF)

    LaunchedEffect(Unit) {
        viewModel.fetchInterests()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "What do you like?",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Select all that apply",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(interests) { interest ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleInterestSelection(interest.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedInterests.contains(interest.id),
                        onCheckedChange = { viewModel.toggleInterestSelection(interest.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = interest.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.submitSelectedInterests(token) {
                    onNavigateHome()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedInterests.isNotEmpty() && uiState !is AuthUiState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = mainColor
            )
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Finish Setup")
            }
        }
    }
}