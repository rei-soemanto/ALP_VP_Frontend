package com.example.alp_vp_frontend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

sealed interface EditProfileUiState {
    object Idle : EditProfileUiState
    object Loading : EditProfileUiState
    object Success : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
}

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private fun prepareImagePart(context: Context, uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return try {
            val file = File(context.cacheDir, "temp_profile_image.jpg")
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("avatar", file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateProfile(
        context: Context,
        fullName: String,
        about: String,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading
            try {
                val imagePart = prepareImagePart(context, imageUri)

                userRepository.updateUser(fullName, about, imagePart)

                _uiState.value = EditProfileUiState.Success
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: "Update failed")
            }
        }
    }
}