package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditStoryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditStoryViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
