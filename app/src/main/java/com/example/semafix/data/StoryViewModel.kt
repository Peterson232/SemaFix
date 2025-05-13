package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semafix.models.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> get() = _stories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _stories.value = repository.getAllStories()
            } catch (e: Exception) {
                _stories.value = emptyList()
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun toggleLike(story: Story, userId: String) {
        viewModelScope.launch {
            repository.toggleLike(story.id, userId)
            fetchStories() // Refresh stories if needed
        }
    }



}
