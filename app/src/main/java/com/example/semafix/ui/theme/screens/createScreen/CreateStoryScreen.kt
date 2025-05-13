package com.example.semafix.ui.theme.screens.createScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.semafix.data.AuthViewModel
import com.example.semafix.data.CreateStoryModel
import com.example.semafix.data.CreateStoryModelFactory
import com.example.semafix.navigation.Routes
import com.example.semafix.network.ImgurApiService
import com.example.semafix.ui.theme.Primary
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateStoryScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CreateStoryModel = viewModel(
        factory = CreateStoryModelFactory(ImgurApiService.create())
    )

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imageUrl by viewModel.imageUrl.collectAsState()
    val uploading by viewModel.uploading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var location by remember { mutableStateOf("") }

    // For capturing photo
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    // Launch gallery
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { imageUri = it }
        }

    // Launch camera
    val takePhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) imageUri = photoUri.value
        }
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            // Compress image before upload
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
            FileOutputStream(compressedFile).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output) // 70% quality
            }
            compressedFile
        } catch (e: Exception) {
            Toast.makeText(context, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }


    fun launchCamera() {
        val file = File(context.cacheDir, "story_photo.jpg").apply {
            createNewFile()
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        photoUri.value = uri
        takePhotoLauncher.launch(uri)
    }
    LaunchedEffect(imageUrl) {
        imageUrl?.let {
            // Navigate back or show success when image is uploaded
            navController.navigate("news")
            Toast.makeText(context, "Story created successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearState()
        }
    }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                listOf("Home", "Create", "News", "Profile").forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            when(index) {
                                0 -> navController.navigate(Routes.Dashboard.route)
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Existing content with new fields
            Text("Create New Report", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Add location field
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )


                Spacer(modifier = Modifier.height(16.dp))

                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { launchCamera() }) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Camera")
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                    Button(onClick = { pickImageLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = "Gallery")
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (validateInputs(title, description, location, imageUri)) {
                            val file = uriToFile(context, imageUri!!)
                            viewModel.uploadStory(
                                file = file!!,
                                title = title,
                                description = description,
                                location = location
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uploading
                ) {
                    Text("Submit Report")
                }
            }
        }
    }

private fun validateInputs(
    title: String,
    description: String,
    location: String,
    imageUri: Uri?
): Boolean {
    return title.isNotEmpty() &&
            description.isNotEmpty() &&
            location.isNotEmpty() &&
            imageUri != null
}

