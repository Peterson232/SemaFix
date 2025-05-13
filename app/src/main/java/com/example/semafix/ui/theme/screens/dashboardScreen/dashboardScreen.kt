package com.example.semafix.ui.screens.dashboard

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semafix.data.AuthViewModel
import com.example.semafix.navigation.Routes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.semafix.R
import com.example.semafix.ui.theme.DarkOverlay
import com.example.semafix.ui.theme.Primary
import com.example.semafix.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedItem = remember { mutableStateOf(0) }
    val showSupportDialog = remember { mutableStateOf(false) }
    val recentActivities = remember { mutableStateListOf("Story created", "Profile updated", "New notification") }

    Scaffold(
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
                            when(index) {
                                1 -> navController.navigate("create_screen")
                                2 -> navController.navigate("news")
                                3 -> navController.navigate("profile_screen")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when(index) {
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
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Modern gradient background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.1f),
                                Secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
            Box() {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "background image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                item {
                    UserInfoSection(navController)
                    Spacer(modifier = Modifier.height(32.dp))
                    QuickActionsGrid(navController)
                    Spacer(modifier = Modifier.height(24.dp))
                    LiveNewsSection(youtubeUrl = "https://www.youtube.com/live/YDvsBbKfLPA?si=eaRxxeM9HDTbaxia")

                }
            }

            // Modern FAB for quick story creation
            FloatingActionButton(
                onClick = { navController.navigate("create_screen") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Primary
            ) {
                Icon(Icons.Filled.Add, "Create Story")
            }
        }
    }

}

@Composable
private fun UserInfoSection(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("profile_screen") },
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Secondary)
            ) {
                Icon(
                    Icons.Filled.Person,
                    "Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
                IconButton(
                    onClick = { /* Handle profile picture update */ },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.background(Primary, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Welcome, User",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Online", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Dashboard.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Log Out")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(navController: NavController) {
    val actions = listOf(
        Triple("Read", Icons.AutoMirrored.Filled.Article, "news"),
        Triple("Edit", Icons.Filled.Edit, "edit_story/{storyId}"),
        Triple("Posts", Icons.Filled.List, "profile_screen"),
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Manage Stories",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            actions.forEach { (title, icon, route) ->
                ElevatedCard(
                    onClick = { navController.navigate(route) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = title,
                            modifier = Modifier.size(32.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveNewsSection(youtubeUrl: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(220.dp), // You can adjust height as needed
        shape = RoundedCornerShape(24.dp)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    loadUrl(youtubeUrl)
                }
            },
            update = { it.loadUrl(youtubeUrl) }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(rememberNavController())
}