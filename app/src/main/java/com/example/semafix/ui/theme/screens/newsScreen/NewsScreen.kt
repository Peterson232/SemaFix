package com.example.semafix.ui.theme.screens.newsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.semafix.data.NewsViewModel
import com.example.semafix.data.StoryRepository
import com.example.semafix.data.StoryViewModel
import com.example.semafix.data.StoryViewModelFactory
import com.example.semafix.models.Story
import com.example.semafix.navigation.Routes
import com.example.semafix.ui.theme.Primary
import com.example.semafix.ui.theme.Secondary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    val repository = remember { StoryRepository(FirebaseFirestore.getInstance()) }
    val viewModel: StoryViewModel = viewModel(factory = StoryViewModelFactory(repository))
    val stories by viewModel.stories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val selectedItem = remember { mutableIntStateOf(2) }



    LaunchedEffect(Unit) {
        viewModel.fetchStories()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("News Feed", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Primary.copy(alpha = 0.05f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                listOf("Home", "Create", "News", "Profile").forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedItem.value == index,
                        onClick = {
                            selectedItem.value = index
                            when (index) {
                                0 -> navController.navigate(Routes.Dashboard.route)
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
                                    2 -> Icons.Filled.Newspaper
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchStories() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                stories.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Article,
                                contentDescription = "No news",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No stories available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(stories, key = { it.id }) { story ->
                            StoryItem(story = story)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryItem(story: Story) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(story.location, style = MaterialTheme.typography.labelMedium)
                Text(story.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(story.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (story.imageUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(model = story.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(story.description, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            val context = LocalContext.current
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        StoryRepository(FirebaseFirestore.getInstance()).likeStory(story.id, userId)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${story.likes}")
                }

                // Status badge
                Badge(
                    containerColor = when (story.status) {
                        "Resolved" -> Color(0xFF4CAF50)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    content = {
                        Text(
                            if (story.status == "Pending") "Open" else story.status,  // Renaming "Pending"
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                )
            }

        }
    }
}
