package com.example.semafix.ui.theme.screens.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.semafix.data.ProfileViewModel
import com.example.semafix.models.Story
import com.example.semafix.models.User
import com.example.semafix.ui.screens.dashboard.DashboardScreen

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState(initial = User())
    val userStories by viewModel.userStories.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedStoryId by remember { mutableStateOf<String?>(null) }
    val loading = user == null || userStories.isEmpty()  // Simplified check

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
        viewModel.fetchUserStories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Profile Section
            user?.let { nonNullUser ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (nonNullUser.profileImageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(nonNullUser.profileImageUrl),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Default profile",
                            modifier = Modifier.size(120.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(nonNullUser.name, style = MaterialTheme.typography.headlineSmall)
                    Text("@${nonNullUser.username}", style = MaterialTheme.typography.bodyMedium)
                    Text(nonNullUser.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${nonNullUser.county}, ${nonNullUser.constituency}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(nonNullUser.about, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stories Section
                Text("Your Stories", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (userStories.isEmpty()) {
                    Text("No stories available")
                } else {
                    LazyColumn {
                        items(userStories) { story ->
                            EditableStoryItem(
                                story = story,
                                onEditClick = { navController.navigate("edit_story/${story.id}") },
                                onDeleteClick = {
                                    selectedStoryId = story.id
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Story?") },
                text = { Text("This action cannot be undone") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedStoryId?.let { viewModel.deleteStory(it) }
                        showDeleteDialog = false
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
@Composable
fun EditableStoryItem(
    story: Story,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(story.title, fontWeight = FontWeight.Bold)
        Text(story.description)
        if (story.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(story.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onEditClick) {
                Text("Edit")
            }
            Button(onClick = onDeleteClick) {
                Text("Delete")
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}
