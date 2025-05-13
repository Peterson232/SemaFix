package com.example.semafix.ui.theme.screens.profileScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semafix.data.ProfileViewModel
import com.example.semafix.data.ProfileViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
    val user by viewModel.user.collectAsState()

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var county by remember { mutableStateOf(TextFieldValue("")) }
    var constituency by remember { mutableStateOf(TextFieldValue("")) }
    var about by remember { mutableStateOf(TextFieldValue("")) }

    // Initialize form fields with user data
    LaunchedEffect(user) {
        user?.let {
            name = TextFieldValue(it.name ?: "")
            username = TextFieldValue(it.username ?: "")
            phone = TextFieldValue(it.phoneNumber ?: "")
            county = TextFieldValue(it.county ?: "")
            constituency = TextFieldValue(it.constituency ?: "")
            about = TextFieldValue(it.about ?: "")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
            OutlinedTextField(value = county, onValueChange = { county = it }, label = { Text("County") })
            OutlinedTextField(value = constituency, onValueChange = { constituency = it }, label = { Text("Constituency") })
            OutlinedTextField(value = about, onValueChange = { about = it }, label = { Text("About") })

            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        name.text,
                        username.text,
                        phone.text,
                        county.text,
                        constituency.text,
                        about.text
                    )
                    navController.popBackStack() // go back to profile
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
