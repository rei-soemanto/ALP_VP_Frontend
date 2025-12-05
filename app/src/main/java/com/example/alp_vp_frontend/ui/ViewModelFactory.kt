package com.example.alp_vp_frontend.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_vp_frontend.MyApplication
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.ui.viewmodel.AuthViewModel
import com.example.alp_vp_frontend.ui.viewmodel.CommentViewModel
import com.example.alp_vp_frontend.ui.viewmodel.PostViewModel
import com.example.alp_vp_frontend.ui.viewmodel.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Auth ViewModel
        initializer {
            val application = inventoryApplication()
            val dataStoreManager = DataStoreManager(application.applicationContext)

            AuthViewModel(
                repository = inventoryApplication().container.authRepository,
                dataStoreManager = dataStoreManager
            )
        }

        initializer {
            val app = inventoryApplication()
            ProfileViewModel(
                userRepository = app.container.userRepository,
                postRepository = app.container.postRepository,
                dataStoreManager = DataStoreManager(app.applicationContext)
            )
        }

        initializer {
            val app = inventoryApplication()
            PostViewModel(
                apiService = app.container.postApiService,
                dataStore = DataStoreManager(app.applicationContext)
            )
        }

        initializer {
            val app = inventoryApplication()
            CommentViewModel(
                postRepository = app.container.postRepository,
                dataStoreManager = DataStoreManager(app.applicationContext)
            )
        }
    }
}

fun CreationExtras.inventoryApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)