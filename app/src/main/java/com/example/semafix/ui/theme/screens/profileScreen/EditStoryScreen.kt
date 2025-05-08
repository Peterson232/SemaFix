package com.example.semafix.ui.theme.screens.profileScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.semafix.models.Story
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Composable
fun EditStoryScreen(
    navController: NavController,
    storyId: String, // Pass the story ID for editing
    viewModel: EditStoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val story by viewModel.story.collectAsState()

    var title by remember { mutableStateOf(story?.title ?: "") }
    var description by remember { mutableStateOf(story?.description ?: "") }

    LaunchedEffect(storyId) {
        viewModel.getStory(storyId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Story", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    viewModel.updateStory(storyId, title, description)
                    Toast.makeText(context, "Story updated successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigateUp() // Go back to profile page
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@HiltViewModel
class EditStoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _story = MutableStateFlow<Story?>(null)
    val story: StateFlow<Story?> = _story

    fun getStory(storyId: String) {
        firestore.collection("stories")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                _story.value = document.toObject(Story::class.java)
            }
    }

    fun updateStory(storyId: String, title: String, description: String) {
        firestore.collection("stories")
            .document(storyId)
            .update("title", title, "description", description)
            .addOnSuccessListener {
                // Successfully updated
            }
            .addOnFailureListener { e ->
                Log.e("EditStoryViewModel", "Error updating story", e)
            }
    }
}
