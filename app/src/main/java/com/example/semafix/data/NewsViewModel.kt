package com.example.semafix.data

import androidx.lifecycle.ViewModel
import com.example.semafix.models.Story
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsViewModel : ViewModel() {
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchStories() {
        firestore.collection("stories")
            .get()
            .addOnSuccessListener { result ->
            }
        _isLoading.value = true
        try {
            // Replace with actual Firestore fetch
            _stories.value = listOf(
                Story(
                    id = "1",
                    title = "First Story",
                    description = "This is the first story",
                    imageUrl = "https://example.com/story1.jpg"
                ),
                Story(
                    id = "2",
                    title = "Second Story",
                    description = "This is the second story",
                    imageUrl = "https://example.com/story2.jpg"
                )
            )
            _errorMessage.value = null
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load stories: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }
}
