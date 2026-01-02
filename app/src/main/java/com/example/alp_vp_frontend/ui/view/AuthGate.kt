package com.example.alp_vp_frontend.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_vp_frontend.ui.routing.Screen
import com.example.alp_vp_frontend.ui.viewmodel.AuthViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ProfileUiState
import com.example.alp_vp_frontend.ui.viewmodel.ProfileViewModel

@Composable
fun AuthGate(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var profileState = profileViewModel.profileState

    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileUiState.Error -> {
                authViewModel.logout {}
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.AuthGate.route) {
                        inclusive = true
                    }
                }
            }

            is ProfileUiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.AuthGate.route) {
                        inclusive = true
                    }
                }
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Color(0xFF6759FF)
        )
    }
}