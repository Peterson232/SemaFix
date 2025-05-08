package com.example.semafix.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.semafix.screens.SplashScreen
import com.example.semafix.ui.screens.dashboard.DashboardScreen
import com.example.semafix.ui.theme.screens.createScreen.CreateStoryScreen
import com.example.semafix.ui.theme.screens.login.LoginScreen
import com.example.semafix.ui.theme.screens.newsScreen.NewsScreen
import com.example.semafix.ui.theme.screens.profileScreen.EditStoryScreen
import com.example.semafix.ui.theme.screens.profileScreen.ProfileScreen
import com.example.semafix.ui.theme.screens.register.RegisterScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }
        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController) // Replace with your actual home screen content
        }
        composable("create_screen") {
            CreateStoryScreen(navController)
        }
        composable("news") { NewsScreen(navController = navController) }

        composable("profile_screen") {
            ProfileScreen(navController)
        }
        composable("edit_story/{storyId}") { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: return@composable
            EditStoryScreen(navController = navController, storyId = storyId)
        }


    }
    }

