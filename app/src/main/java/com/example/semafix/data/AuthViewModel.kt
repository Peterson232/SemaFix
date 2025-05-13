package com.example.semafix.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semafix.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthViewModel: ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    private val _authState = MutableStateFlow<Boolean?>(null)
    val authState: StateFlow<Boolean?> = _authState

    fun registerUser(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: throw Exception("User ID is null")
                val user = UserModel(fullName, email, phone)

                firestore.collection("users")
                    .document(userId)
                    .set(user)
                    .await()

                _authState.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                _authState.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = false
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
