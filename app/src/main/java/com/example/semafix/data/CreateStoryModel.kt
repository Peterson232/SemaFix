package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semafix.models.Story
import com.example.semafix.network.ImgurApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreateStoryModel(
    private val imgurApiService: ImgurApiService,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
                    val link = response.body()?.data?.link
                    _imageUrl.value = link
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

    fun uploadStory(file: File, title: String, description: String, location: String) {
        viewModelScope.launch {
            _uploading.value = true
            try {
                // Upload image to Imgur
                val imageUrl = uploadImageToImgur(file)

                // Save to Firestore
                val story = Story(
                    title = title,
                    description = description,
                    location = location,
                    imageUrl = imageUrl,
                    date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                    userId = auth.currentUser?.uid ?: ""
                )

                firestore.collection("stories")
                    .add(story)
                    .addOnSuccessListener {
                        _imageUrl.value = imageUrl
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Failed to save story: ${e.message}"
                        _imageUrl.value = null
                    }

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _uploading.value = false
            }
        }
    }

    private suspend fun uploadImageToImgur(file: File): String {
        return try {
            // Check file size
            if (file.length() > 10 * 1024 * 1024) { // 10MB
                throw Exception("Image size exceeds 10MB limit")
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = imgurApiService.uploadImage(multipartBody)

            when {
                response.isSuccessful -> {
                    response.body()?.data?.link ?: throw Exception("Empty image URL")
                }

                response.code() == 429 -> throw Exception("API rate limit exceeded")
                else -> throw Exception("Imgur error: ${response.errorBody()?.string()}")
            }
        } catch (e: SocketTimeoutException) {
            throw Exception("Connection timed out. Check your internet connection")
        } catch (e: IOException) {
            throw Exception("Network error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Upload failed: ${e.message}")
        }
    }
}
