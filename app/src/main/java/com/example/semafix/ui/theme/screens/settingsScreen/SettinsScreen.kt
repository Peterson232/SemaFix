package com.example.semafix.ui.theme.screens.settingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(true) } // Initial state for notifications
    var showThemeDialog by remember { mutableStateOf(false) } // State to show the theme dialog

    // Define theme options for selection
    val themeOptions = listOf("Light", "Dark")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Account", style = MaterialTheme.typography.titleMedium)
            Divider()

            Text(
                "Edit Profile",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // Navigate to Edit Profile screen
                        navController.navigate("edit_profile")
                    }
            )

            Text(
                "Change Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // Navigate to Change Password screen
                        navController.navigate("change_password")
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("App Preferences", style = MaterialTheme.typography.titleMedium)
            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifications", modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            Text(
                "Theme",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { showThemeDialog = true }
            )


            Spacer(modifier = Modifier.height(24.dp))

            Text("Privacy & Security", style = MaterialTheme.typography.titleMedium)
            Divider()

            Text(
                "Blocked Users",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // TODO: Show list of blocked users
                    }
            )

            Text(
                "App Permissions",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // TODO: Open permissions management
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Logout",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // TODO: Implement logout logic and redirect to login screen
                        navController.navigate("login") {
                            popUpTo("profile_screen") { inclusive = true }
                        }
                    }
            )
        }
    }
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = true },
            title = { Text("Select Theme") },
            text = {
                Column {
                    themeOptions.forEach { theme ->
                        Text(
                            text = theme,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Handle theme selection logic here
                                    when (theme) {
                                        "Light" -> {
                                            // Set light theme
                                        }
                                        "Dark" -> {
                                            // Set dark theme
                                        }
                                    }
                                    showThemeDialog = true
                                }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showThemeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
