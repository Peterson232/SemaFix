package com.example.semafix.data

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semafix.models.Story
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditStoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _story = MutableStateFlow<Story?>(null)
    val story: StateFlow<Story?> = _story

    fun getStory(storyId: String) {
        firestore.collection("stories").document(storyId).get()
            .addOnSuccessListener { document ->
                _story.value = document.toObject(Story::class.java)
            }
            .addOnFailureListener { e ->
                Log.e("EditStoryViewModel", "Error fetching story", e)
            }
    }

    fun updateStory(storyId: String, title: String, description: String, status: String) {
        firestore.collection("stories").document(storyId)
            .update(
                mapOf(
                    "title" to title,
                    "description" to description,
                    "status" to status
                )
            )
            .addOnFailureListener { e ->
                Log.e("EditStoryViewModel", "Error updating story", e)
            }
    }
}
