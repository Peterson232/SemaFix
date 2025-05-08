package com.example.semafix.data

import androidx.lifecycle.ViewModel
import com.example.semafix.models.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsViewModel : ViewModel() {

    // A MutableStateFlow holding a list of stories
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> get() = _stories

    // Method to fetch stories (e.g., from a repository or API)
    fun fetchStories() {
        // Simulating a fetch with some mock data
        _stories.value = listOf(
            Story(id = "1", title = "First Story", description = "This is the first story", imageUrl = "https://example.com/story1.jpg"),
            Story(id = "2", title = "Second Story", description = "This is the second story", imageUrl = "https://example.com/story2.jpg")
        )
    }
}