package com.example.semafix.ui.theme.screens.newsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.semafix.data.StoryViewModel
import com.example.semafix.models.Story
import com.example.semafix.ui.screens.dashboard.DashboardScreen

@Composable
fun NewsScreen(navController: NavController) {
    val viewModel: StoryViewModel = hiltViewModel()
    val stories = viewModel.stories.collectAsState(initial = emptyList()).value

    LaunchedEffect(Unit) {
        viewModel.fetchStories()  // Fetch stories when the screen is loaded
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("News", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (stories.isEmpty()) {
            Text("No news available", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(stories, key = { story -> story.id }) { story ->
                    StoryItem(story = story)
                }
            }
        }
    }
}

@Composable
fun StoryItem(story: Story) {
    // Display individual story item
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(story.title, fontWeight = FontWeight.Bold)
        Text(story.description)
        Image(painter = rememberAsyncImagePainter(story.imageUrl), contentDescription = null)
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewsScreenPreview() {
    NewsScreen(rememberNavController())
}
