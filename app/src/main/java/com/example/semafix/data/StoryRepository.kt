package com.example.semafix.data

import com.example.semafix.models.Story
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllStories(): List<Story> {
        return firestore.collection("stories")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val story = document.toObject(Story::class.java)
                story?.copy(id = document.id)
            }
    }
}