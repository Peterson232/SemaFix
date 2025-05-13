package com.example.semafix.ui.theme.screens.profileScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Confirm New Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            if (successMessage != null) {
                Text(successMessage!!, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    if (newPassword != confirmNewPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    user?.let {
                        reauthenticateAndChangePassword(it, currentPassword, newPassword,
                            onSuccess = {
                                successMessage = "Password updated successfully"
                                errorMessage = null
                                navController.popBackStack()
                            },
                            onError = { error ->
                                errorMessage = error
                                successMessage = null
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Password")
            }
        }
    }
}
fun reauthenticateAndChangePassword(
    user: FirebaseUser,
    currentPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val email = user.email
    if (email == null) {
        onError("No email found for user.")
        return
    }

    val credential = EmailAuthProvider.getCredential(email, currentPassword)
    user.reauthenticate(credential)
        .addOnSuccessListener {
            user.updatePassword(newPassword)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Failed to update password.") }
        }
        .addOnFailureListener { onError(it.localizedMessage ?: "Reauthentication failed.") }
}

