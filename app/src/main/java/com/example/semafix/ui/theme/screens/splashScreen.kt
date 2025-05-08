package com.example.semafix.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.semafix.R
import com.example.semafix.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // Animation states
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    // Dark gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF121212)
        )
    )

    LaunchedEffect(Unit) {
        // Start animations
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000)
        )

        delay(2000L) // Total 2.5s delay with animations

        // Navigation logic
        val destination = if (auth.currentUser != null) Routes.Dashboard.route else Routes.Login.route
        navController.navigate(destination) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        // Animated background elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f)
            )
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Sema Fix",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5F5F5),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Let's fix the community together!",
                fontSize = 18.sp,
                color = Color(0xFFA0A0A0),
                fontWeight = FontWeight.Medium
            )

            // Animated progress indicator
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
    }
}