package com.example.semafix.data

import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.semafix.models.Story
import com.example.semafix.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)  // Changed from Profile to User
    val user: StateFlow<User?> = _user

    private val _userStories = MutableStateFlow<List<Story>>(emptyList())
    val userStories: StateFlow<List<Story>> = _userStories

    fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)  // Changed to User
                _user.value = user
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error fetching user data", e)
            }
    }

    fun fetchUserStories() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("stories")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                val stories = result.documents.mapNotNull { it.toObject(Story::class.java)?.copy(id = it.id) }
                _userStories.value = stories
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error fetching user stories", e)
            }
    }

    fun deleteStory(storyId: String) {
        firestore.collection("stories")
            .document(storyId)
            .delete()
            .addOnSuccessListener {
                // Remove the deleted story from the local list
                _userStories.value = _userStories.value.filterNot { it.id == storyId }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error deleting story", e)
            }
    }
}
