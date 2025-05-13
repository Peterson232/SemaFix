package com.example.semafix.ui.theme.screens.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.semafix.data.ProfileViewModel
import com.example.semafix.data.ProfileViewModelFactory
import com.example.semafix.models.Story
import com.example.semafix.models.User
import com.example.semafix.ui.theme.Primary
import com.example.semafix.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
    val user by viewModel.user.collectAsState()
    val userStories by viewModel.userStories.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var storyToDelete by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfilePicture(it.toString()) }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
        viewModel.fetchUserStories()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("manage_account")
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out my profile: ${user?.username}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                listOf("Home", "Create", "News", "Profile").forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = title == "Profile",
                        onClick = {
                            when (index) {
                                0 -> navController.navigate("dashboard")
                                1 -> navController.navigate("create_screen")
                                2 -> navController.navigate("news")
                                3 -> navController.navigate("profile_screen")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.Home
                                    1 -> Icons.Filled.AddCircle
                                    2 -> Icons.Filled.Article
                                    3 -> Icons.Filled.Person
                                    else -> Icons.Filled.Person
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { padding ->
        val resolvedCount = userStories.count { it.status == "Resolved" }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    user?.profileImageUrl?.let {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(Icons.Default.Person, contentDescription = null)
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(user?.username ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(user?.name ?: "", fontSize = 16.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val likedCount = userStories.sumOf { it.likedBy?.size ?: 0 }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatItem(label = "Posts", value = userStories.size.toString())
                StatItem(label = "Likes", value = likedCount.toString())
                StatItem(label = "Resolved", value = resolvedCount.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            user?.let {
                Text("Phone: ${it.phoneNumber}")
                Text("Location: ${it.county}, ${it.constituency}")
                Text("About: ${it.about}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Your Stories", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (userStories.isEmpty()) {
                Text("No stories yet!", color = Color.Gray)
            } else {
                userStories.forEach { story ->
                    StoryCard(
                        story = story,
                        onEdit = { navController.navigate("edit_story/${story.id}") },
                        onDelete = {
                            storyToDelete = story.id
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDeleteDialog && storyToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Story") },
                text = { Text("Are you sure you want to delete this story?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteStory(storyToDelete!!)
                        showDeleteDialog = false
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
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
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.Gray)
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}