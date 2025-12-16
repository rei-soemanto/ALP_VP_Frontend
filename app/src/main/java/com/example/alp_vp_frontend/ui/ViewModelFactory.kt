package com.example.alp_vp_frontend.ui

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_vp_frontend.MyApplication
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.ui.viewmodel.AuthViewModel
import com.example.alp_vp_frontend.ui.viewmodel.CommentViewModel
import com.example.alp_vp_frontend.ui.viewmodel.EditProfileViewModel
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = inventoryApplication()

            AuthViewModel(
                repository = app.container.authRepository
            )
        }

        initializer {
            val app = inventoryApplication()
            ProfileViewModel(
                userRepository = app.container.userRepository,
                postRepository = app.container.postRepository
            )
        }

        initializer {
            val app = inventoryApplication()
            PostViewModel(
                repository = app.container.postRepository
            )
        }

        initializer {
            val app = inventoryApplication()
            CommentViewModel(
                postRepository = app.container.postRepository
            )
        }

        initializer {
            val app = inventoryApplication()
            EditProfileViewModel(
                userRepository = app.container.userRepository
            )
        }
    }
}

fun CreationExtras.inventoryApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)