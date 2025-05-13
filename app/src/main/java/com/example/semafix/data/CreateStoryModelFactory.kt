package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.semafix.network.ImgurApiService

class CreateStoryModelFactory(private val apiService: ImgurApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateStoryModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateStoryModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}