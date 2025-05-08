package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semafix.models.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> get() = _stories

    fun fetchStories() {
        viewModelScope.launch {
            try {
                _stories.value = repository.getAllStories()
            } catch (e: Exception) {
                // Handle error
                _stories.value = emptyList()
            }
        }
    }
}
