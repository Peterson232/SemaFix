package com.example.semafix.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.semafix.models.Story
import com.example.semafix.models.User
import com.example.semafix.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.filterNot

class ProfileViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
//    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : ViewModel() {

    private val currentUserId get() = auth.currentUser?.uid ?: ""

    private val _user = MutableStateFlow<User?>(null)
    val user: MutableStateFlow<User?> = _user

    private val _userStories = MutableStateFlow<List<Story>>(emptyList())
    val userStories: StateFlow<List<Story>> = _userStories

    fun updateProfilePicture(imageUrl: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                fetchUserData() // refresh profile after update
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Failed to update profile picture", e)
            }
    }

    fun fetchUserData() {
        val uid = currentUserId
        if (uid.isEmpty()) return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)?.copy(
                    postCount = document.getLong("postCount")?.toInt() ?: 0,
                    resolvedCount = document.getLong("resolvedCount")?.toInt() ?: 0
                )
                _user.value = user
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error fetching user data", e)
            }
    }

    fun fetchUserStories() {
        val uid = currentUserId
        if (uid.isEmpty()) return

        firestore.collection("stories")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProfileViewModel", "Listen failed", error)
                    return@addSnapshotListener
                }

                val stories = snapshot?.documents?.mapNotNull {
                    it.toObject(Story::class.java)?.copy(id = it.id)
                } ?: emptyList()

                _userStories.value = stories
            }
    }

    fun deleteStory(storyId: String) {
        firestore.collection("stories")
            .document(storyId)
            .delete()
            .addOnSuccessListener {
                _userStories.value = _userStories.value.filterNot { it.id == storyId }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error deleting story", e)
            }
    }
    fun updateUserProfile(
        name: String,
        username: String,
        phoneNumber: String,
        county: String,
        constituency: String,
        about: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "username" to username,
            "phoneNumber" to phoneNumber,
            "county" to county,
            "constituency" to constituency,
            "about" to about
        )
        firestore.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                fetchUserData()
            }
    }

}

//class ProfileViewModelFactory : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ProfileViewModel() as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
