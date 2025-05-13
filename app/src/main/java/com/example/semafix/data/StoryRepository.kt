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
    suspend fun toggleLike(storyId: String, userId: String) {
        val storyRef = FirebaseFirestore.getInstance().collection("stories").document(storyId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val snapshot = transaction.get(storyRef)
            val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()

            val updatedLikes = if (userId in likedBy) {
                likedBy - userId // unlike
            } else {
                likedBy + userId // like
            }

            transaction.update(storyRef, "likedBy", updatedLikes)
        }
    }

    suspend fun updateStoryLikes(storyId: String, likedBy: List<String>) {
        firestore.collection("stories")
            .document(storyId)
            .update("likedBy", likedBy)
    }
    suspend fun likeStory(storyId: String, userId: String) {
        val docRef = firestore.collection("stories").document(storyId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()
            val currentLikes = snapshot.getLong("likes") ?: 0L

            if (userId !in likedBy) {
                transaction.update(docRef, mapOf(
                    "likes" to currentLikes + 1,
                    "likedBy" to likedBy + userId
                ))
            }
        }
    }

    fun toggleResolvedStatus(storyId: String, currentStatus: Boolean) {
        firestore.collection("stories").document(storyId)
            .update("resolved", !currentStatus)
    }


}