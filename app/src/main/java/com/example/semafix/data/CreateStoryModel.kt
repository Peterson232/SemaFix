package com.example.semafix.data

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semafix.network.ImgurApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateStoryModel @Inject constructor(
    private val imgurApiService: ImgurApiService
) : ViewModel() {

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl = _imageUrl.asStateFlow()

    private val _uploading = MutableStateFlow(false)
    val uploading = _uploading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            _uploading.value = true
            _errorMessage.value = null

            try {
                val requestFile = imageFile
                    .asRequestBody("image/*".toMediaTypeOrNull())

                val multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestFile
                )

                val response = imgurApiService.uploadImage(multipartBody)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data?.link
                    _imageUrl.value = imageUrl
                } else {
                    _errorMessage.value = "Upload failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.localizedMessage}"
            } finally {
                _uploading.value = false
            }
        }
    }

    fun clearState() {
        _imageUrl.value = null
        _uploading.value = false
        _errorMessage.value = null
    }
}